package com.mylaesoftware;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;
import com.mylaesoftware.mappers.BasicMappers;
import com.mylaesoftware.mappers.CollectionsMappers;
import com.mylaesoftware.mappers.ConfigMapper;
import com.mylaesoftware.mappers.NoMapper;
import com.mylaesoftware.runtime.DefineClassStrategy;
import com.mylaesoftware.runtime.LookupDefineClassStrategy;
import com.mylaesoftware.validators.ConfigValidationException;
import com.mylaesoftware.validators.ConfigValidator;
import com.typesafe.config.Config;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.I2C;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;
import static org.objectweb.asm.Opcodes.V1_8;

public class RuntimeConfigComposer {
  private static final String PACKAGE = RuntimeConfigComposer.class.getPackage().getName()
      .replace('.', '/');

  private static final Map<Class<?>, Class<?>> COLLECTIONS_MAPPERS;

  static {
    COLLECTIONS_MAPPERS = new HashMap<>();
    COLLECTIONS_MAPPERS.put(String.class, CollectionsMappers.StringListM.class);
    COLLECTIONS_MAPPERS.put(Duration.class, CollectionsMappers.DurationListM.class);
    COLLECTIONS_MAPPERS.put(Boolean.class, CollectionsMappers.BooleanListM.class);
    COLLECTIONS_MAPPERS.put(Integer.class, CollectionsMappers.IntListM.class);
    COLLECTIONS_MAPPERS.put(Long.class, CollectionsMappers.LongListM.class);
    COLLECTIONS_MAPPERS.put(Number.class, CollectionsMappers.NumberListM.class);
    COLLECTIONS_MAPPERS.put(Double.class, CollectionsMappers.DoubleListM.class);
    COLLECTIONS_MAPPERS.put(Config.class, CollectionsMappers.ConfigListM.class);
  }

  private static RuntimeConfigComposer SINGLETON = null;
  private static final Object LOCK = new Object();

  private final DefineClassStrategy dcs;
  private final AtomicInteger ai = new AtomicInteger(0);

  public RuntimeConfigComposer(DefineClassStrategy dcs) {
    this.dcs = dcs;
  }

  private static RuntimeConfigComposer ensureSingleton() {
    synchronized (LOCK) {
      if (SINGLETON == null) {
        SINGLETON = new RuntimeConfigComposer(new LookupDefineClassStrategy());
      }
      return SINGLETON;
    }
  }

  public static <T> T wire(Class<T> clz, Config config) {
    return ensureSingleton().wireConfig(clz, config);
  }

  public static <T> ConfigComposerFactory<T> factory(Class<T> clz) {
    return ensureSingleton().generateFactory(clz);

  }

  public <T> T wireConfig(Class<T> clz, Config config) {
    // IntelliJ bug - ecj can't cope without the intermediate variable :|
    final ConfigComposerFactory<T> factory = factory(generate(clz));
    return factory.wire(config);
  }

  public <T> ConfigComposerFactory<T> generateFactory(Class<T> clz) {
    return new DefaultConfigComposerFactory<>(clz);
  }

  private <T> Class<? extends T> generate(Class<T> clz) {
    if (!isValidConfigInterface(Objects.requireNonNull(clz))) {
      throw new IllegalArgumentException("Non config class");
    } else {
      return implementInterface(clz);
    }
  }

  private boolean isValidConfigInterface(Class<?> clz) {
    return clz.isInterface() && clz.isAnnotationPresent(ConfigType.class);
  }

  private <T> Class<? extends T> implementInterface(Class<T> iface) {
    final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    final String generatedName = PACKAGE + "/" + iface.getSimpleName() + "Impl" + ai.getAndIncrement();

    cw.visit(V1_8,
        ACC_PUBLIC | ACC_SYNTHETIC,
        generatedName,
        null,
        "java/lang/Object",
        new String[]{Type.getType(iface).getInternalName()}
    );

    generateConfigField(cw);
    generateConstructor(cw, generatedName);

    for (Method m : iface.getMethods()) {
      final ConfigValue value = m.getAnnotation(ConfigValue.class);

      if (value != null) {
        generateConfigValueMethod(cw, m, generatedName, value);
      }
    }

    cw.visitSource("<Generated>", null);
    cw.visitEnd();

    return dcs.defineClass(iface, cw.toByteArray());
  }

  private boolean isIreturnType(Class<?> primClass) {
    return byte.class.equals(primClass) || Byte.class.equals(primClass) || char.class.equals(primClass)
        || Character.class.equals(primClass) || short.class.equals(primClass) || Short.class.equals(primClass)
        || int.class.equals(primClass) || Integer.class.equals(primClass);
  }

  private boolean isJreturnType(Class<?> primClass) {
    return long.class.equals(primClass) || Long.class.equals(primClass);
  }

  private boolean isDreturnType(Class<?> primClass) {
    return float.class.equals(primClass) || Float.class.equals(primClass) || double.class.equals(primClass)
        || Double.class.equals(primClass);

  }

  private boolean isZreturnType(Class<?> primClass) {
    return boolean.class.equals(primClass) || Boolean.class.equals(primClass);
  }

  private boolean isBoxType(Class<?> clz) {
    return Byte.class.equals(clz) || Character.class.equals(clz) || Short.class.equals(clz) ||
        Integer.class.equals(clz) || Long.class.equals(clz) || Float.class.equals(clz) ||
        Double.class.equals(clz) || Boolean.class.equals(clz);
  }

  private boolean isByteType(Class<?> clz) {
    return byte.class.equals(clz) || Byte.class.equals(clz);
  }

  private boolean isCharType(Class<?> clz) {
    return char.class.equals(clz) || Character.class.equals(clz);
  }

  private boolean isShortType(Class<?> clz) {
    return short.class.equals(clz) || Short.class.equals(clz);
  }

  private boolean isFloatType(Class<?> clz) {
    return float.class.equals(clz) || Float.class.equals(clz);
  }

  private Class<?> forNameOrRte(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<Class<?>> getSingleTypeParameterClass(java.lang.reflect.Type jtype) {
    final String s = jtype.getTypeName();

    if (s.indexOf('<') == -1 || s.indexOf('>') == -1) {
      return Optional.empty();
    } else {
      return Optional.of(s.substring(s.indexOf('<') + 1, s.indexOf('>')))
          .map(this::forNameOrRte);
    }
  }

  private void generateConfigField(final ClassVisitor cw) {
    cw.visitField(
        ACC_PRIVATE | ACC_FINAL | ACC_SYNTHETIC,
        "config",
        "Lcom/typesafe/config/Config;",
        null,
        null
    ).visitEnd();
  }

  private void generateConstructor(final ClassVisitor cw, final String generatedName) {
    final MethodVisitor ctor = cw.visitMethod(
        ACC_PUBLIC | ACC_SYNTHETIC,
        "<init>",
        "(Lcom/typesafe/config/Config;)V",
        null,
        null
    );

    ctor.visitCode();
    ctor.visitVarInsn(ALOAD, 0);
    ctor.visitInsn(DUP);
    ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    ctor.visitVarInsn(ALOAD, 1);
    ctor.visitFieldInsn(PUTFIELD, generatedName, "config", "Lcom/typesafe/config/Config;");
    ctor.visitInsn(RETURN);
    ctor.visitMaxs(0, 0);
    ctor.visitEnd();
  }

  private void generateConfigValueMethod(
      final ClassVisitor cw,
      final Method m,
      final String generatedName,
      final ConfigValue value
  ) {
    final Class<?> returnType = m.getReturnType();

    final MethodVisitor impl = cw.visitMethod(
        ACC_PUBLIC | ACC_SYNTHETIC,
        m.getName(),
        Type.getMethodDescriptor(m),
        null,
        null
    );

    if (void.class.equals(returnType)) {
      impl.visitInsn(RETURN);
    } else if (String.class.equals(returnType)) {
      generateStringGetter(impl, value, generatedName);
    } else if (isIreturnType(returnType)) {
      generateIntegralGetter(impl, value, generatedName, returnType);
    } else if (isJreturnType(returnType)) {
      generateLongGetter(impl, value, generatedName, returnType);
    } else if (isDreturnType(returnType)) {
      generateFloatingPointGetter(impl, value, generatedName, returnType);
    } else if (isZreturnType(returnType)) {
      generateBoolGetter(impl, value, generatedName, returnType);
    } else if (List.class.equals(returnType)) {
      generateListGetter(impl, value, generatedName, m.getGenericReturnType());
    } else {
      generateMappedAndValidatedAReturn(impl, generatedName, value.atPath(),
          NoMapper.class.equals(value.mappedBy())
              ? BasicMappers.AnyRefM.class
              : value.mappedBy(),
          Object.class, value.validatedBy());
    }

    impl.visitMaxs(0, 0);
    impl.visitEnd();
  }

  /*
   * top-level, clean stack, returns String
   */
  private void generateStringGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName
  ) {
    generateMapperInstantiation(mv, NoMapper.class.equals(value.mappedBy())
        ? BasicMappers.StringM.class
        : value.mappedBy());

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(value.atPath());

    generateMapperApply(mv, String.class);

    generateValidation(mv, value.validatedBy());

    mv.visitInsn(ARETURN);
  }

  /*
   * top-level, clean stack, returns B,C,S,I or appropriate box
   */
  private void generateIntegralGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName,
      final Class<?> returnType
  ) {
    generateMapperInstantiation(mv, NoMapper.class.equals(value.mappedBy())
        ? BasicMappers.IntM.class
        : value.mappedBy());

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(value.atPath());

    generateMapperApply(mv, Integer.class);

    // Do this before unboxing/reboxing or otherwise massaging for return,
    // so validators will always work on the underlying type returned
    // by the Config object. I think this makes the most sense...
    generateValidation(mv, value.validatedBy());

    if (isByteType(returnType)) {
      generateByteReturn(mv, returnType);
    } else if (isCharType(returnType)) {
      generateCharReturn(mv, returnType);
    } else if (isShortType(returnType)) {
      generateShortReturn(mv, returnType);
    } else {
      generateIntReturn(mv, returnType);
    }
  }

  /*
   * top-level, clean stack, returns J or Long
   */
  private void generateLongGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName,
      final Class<?> returnType
  ) {
    generateMapperInstantiation(mv, NoMapper.class.equals(value.mappedBy())
        ? BasicMappers.LongM.class
        : value.mappedBy());

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(value.atPath());

    generateMapperApply(mv, Long.class);

    generateValidation(mv, value.validatedBy());

    if (isBoxType(returnType)) {
      mv.visitInsn(ARETURN);
    } else {
      // unbox
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Long",
          "longValue",
          "()J",
          false
      );

      mv.visitInsn(LRETURN);
    }
  }

  /*
   * top-level, clean stack, returns D, F or appropriate box
   */
  private void generateFloatingPointGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName,
      final Class<?> returnType
  ) {
    generateMapperInstantiation(mv, NoMapper.class.equals(value.mappedBy())
        ? BasicMappers.DoubleM.class
        : value.mappedBy());

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(value.atPath());

    generateMapperApply(mv, Double.class);

    generateValidation(mv, value.validatedBy());

    if (isFloatType(returnType)) {
      generateFloatReturn(mv, returnType);
    } else {
      generateDoubleReturn(mv, returnType);
    }
  }

  /*
   * top-level, clean stack, returns Z or Boolean
   */
  private void generateBoolGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName,
      final Class<?> returnType
  ) {
    generateMapperInstantiation(mv, NoMapper.class.equals(value.mappedBy())
        ? BasicMappers.BooleanM.class
        : value.mappedBy());

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(value.atPath());

    generateMapperApply(mv, Boolean.class);

    generateValidation(mv, value.validatedBy());

    if (isBoxType(returnType)) {
      mv.visitInsn(ARETURN);
    } else {
      // Unbox
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Boolean",
          "booleanValue",
          "()Z",
          false
      );

      mv.visitInsn(IRETURN);
    }
  }

  /*
   * top-level, clean stack, returns List
   */
  private void generateListGetter(
      final MethodVisitor mv,
      final ConfigValue value,
      final String generatedName,
      final java.lang.reflect.Type genericReturnType
  ) {
    final Optional<Class<?>> genericType = getSingleTypeParameterClass(genericReturnType);

    if (genericType.isPresent()) {
      final Class<?> elementClass = genericType.get();
      if (COLLECTIONS_MAPPERS.containsKey(elementClass)) {
        final Class<?> mapperClass = COLLECTIONS_MAPPERS.get(elementClass);

        generateMappedAndValidatedAReturn(mv, generatedName, value.atPath(),
            NoMapper.class.equals(value.mappedBy())
                ? mapperClass
                : value.mappedBy(),
            List.class, value.validatedBy());
      } else {
        generateMappedAndValidatedAReturn(mv, generatedName, value.atPath(),
            NoMapper.class.equals(value.mappedBy())
                ? CollectionsMappers.AnyRefListM.class
                : value.mappedBy(),
            List.class, value.validatedBy());
      }
    } else {
      generateMappedAndValidatedAReturn(mv, generatedName, value.atPath(),
          NoMapper.class.equals(value.mappedBy())
              ? CollectionsMappers.AnyRefListM.class
              : value.mappedBy(),
          List.class, value.validatedBy());
    }
  }

  /*
   * ..., Integer => (B or Byte returned)
   */
  private void generateByteReturn(final MethodVisitor mv, final Class<?> returnType) {
    // Unbox
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/Integer",
        "byteValue",
        "()B",
        false
    );

    if (isBoxType(returnType)) {
      // Rebox
      generateBoxReturn(mv, byte.class, Byte.class);
    } else {
      mv.visitInsn(IRETURN);
    }
  }

  /*
   * ..., Integer => (C or Character returned)
   */
  private void generateCharReturn(final MethodVisitor mv, final Class<?> returnType) {
    // Unbox - handle char a bit specially (primitive cast) due to differences in box API
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/Integer",
        "intValue",
        "()I",
        false
    );

    mv.visitInsn(I2C);

    if (isBoxType(returnType)) {
      generateBoxReturn(mv, char.class, Character.class);
    } else {
      mv.visitInsn(IRETURN);
    }
  }

  /*
   * ..., Integer => (S or Short returned)
   */
  private void generateShortReturn(final MethodVisitor mv, final Class<?> returnType) {
    // Unbox
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/Integer",
        "shortValue",
        "()S",
        false
    );

    if (isBoxType(returnType)) {
      // Rebox
      generateBoxReturn(mv, short.class, Short.class);
    } else {
      mv.visitInsn(IRETURN);
    }
  }

  /*
   * ..., Integer => (I or Integer returned)
   */
  private void generateIntReturn(final MethodVisitor mv, final Class<?> returnType) {
    if (isBoxType(returnType)) {
      mv.visitInsn(ARETURN);
    } else {
      // Unbox
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Integer",
          "intValue",
          "()I",
          false
      );

      mv.visitInsn(IRETURN);
    }
  }

  /*
   * ..., Double => (F or Float returned)
   */
  private void generateFloatReturn(final MethodVisitor mv, final Class<?> returnType) {
    // Unbox
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/Double",
        "floatValue",
        "()F",
        false
    );

    if (isBoxType(returnType)) {
      // Rebox
      generateBoxReturn(mv, float.class, Float.class);
    } else {
      mv.visitInsn(FRETURN);
    }
  }

  /*
   * ..., Double => (D or Double returned)
   */
  private void generateDoubleReturn(final MethodVisitor mv, final Class<?> returnType) {
    if (isBoxType(returnType)) {
      mv.visitInsn(ARETURN);
    } else {
      // Unbox
      mv.visitMethodInsn(
          INVOKEVIRTUAL,
          "java/lang/Double",
          "doubleValue",
          "()D",
          false
      );

      mv.visitInsn(DRETURN);
    }
  }

  /*
   * ..., A => (returns or throws)
   */
  private void generateMappedAndValidatedAReturn(
      final MethodVisitor mv,
      final String generatedName,
      final String configPath,
      final Class<?> mapperClz,
      final Class<?> clz,
      final Class<? extends ConfigValidator<?>>[] validationClasses
  ) {
    generateMapperInstantiation(mv, mapperClz);

    generateLoadConfigField(mv, generatedName);
    mv.visitLdcInsn(configPath);

    generateMapperApply(mv, clz);

    generateValidation(mv, validationClasses);

    mv.visitInsn(ARETURN);
  }

  /*
   * ..., primitive => (returns appropriate box)
   */
  private void generateBoxReturn(final MethodVisitor mv, final Class<?> primType, final Class<?> boxType) {
    mv.visitMethodInsn(
        INVOKESTATIC,
        Type.getInternalName(boxType),
        "valueOf",
        Type.getMethodDescriptor(Type.getType(boxType), Type.getType(primType)),
        false);

    mv.visitInsn(ARETURN);
  }

  /*
   * ...  => ..., ConfigMapper
   */
  private void generateMapperInstantiation(final MethodVisitor mv, final Class<?> mapperClass) {
    mv.visitTypeInsn(NEW, Type.getType(mapperClass).getInternalName());
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL,
        Type.getType(mapperClass).getInternalName(),
        "<init>",
        "()V",
        false
    );
  }

  /*
   * ... => ..., Config
   */
  private void generateLoadConfigField(final MethodVisitor mv, final String generatedName) {
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, generatedName, "config", "Lcom/typesafe/config/Config;");
  }

  /*
   * ..., ConfigMapper, Config, String => ..., <expectedReturnType>
   * Required CHECKCAST is handled here.
   */
  private void generateMapperApply(final MethodVisitor mv, final Class<?> expectedReturnType) {
    mv.visitMethodInsn(
        INVOKEINTERFACE,
        Type.getType(ConfigMapper.class).getInternalName(),
        "apply",
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
        true
    );

    mv.visitTypeInsn(CHECKCAST, Type.getInternalName(expectedReturnType));
  }

  /*
   * ..., A => ..., A (or throw)
   */
  private void generateValidation(
      final MethodVisitor mv,
      final Class<? extends ConfigValidator<?>>[] validatorClasses
  ) {
    if (validatorClasses != null && validatorClasses.length > 0) {
      mv.visitTypeInsn(NEW, "java/util/ArrayList");
      mv.visitInsn(DUP);
      mv.visitLdcInsn(validatorClasses.length);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "java/util/ArrayList",
          "<init>",
          "(I)V",
          false
      );

      for (Class<? extends ConfigValidator<?>> validatorClz : validatorClasses) {
        // Unrolled loop in the generated code...
        generateSingleValidation(mv, validatorClz);
      }

      mv.visitInsn(DUP);
      mv.visitMethodInsn(
          INVOKEINTERFACE,
          "java/util/Collection",
          "isEmpty",
          "()Z",
          true
      );

      Label nothrow = new Label();
      mv.visitJumpInsn(IFNE, nothrow);

      mv.visitTypeInsn(NEW, Type.getInternalName(ConfigValidationException.class));
      mv.visitInsn(DUP_X1);
      mv.visitInsn(SWAP);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          Type.getInternalName(ConfigValidationException.class),
          "<init>",
          "(Ljava/util/Collection;)V",
          false
      );

      mv.visitInsn(ATHROW);

      mv.visitLabel(nothrow);
      mv.visitInsn(POP);
    }
  }

  /*
   * ..., A, Collection => ..., A, Collection
   */
  private void generateSingleValidation(
      final MethodVisitor mv,
      final Class<? extends ConfigValidator<?>> validatorClz
  ) {
    mv.visitInsn(DUP_X1);   // ..., Collection, A, Collection
    mv.visitInsn(SWAP);     // ..., Collection, Collection, A
    mv.visitInsn(DUP_X1);   // ..., Collection, A, Collection, A

    mv.visitTypeInsn(NEW, Type.getInternalName(validatorClz));
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESPECIAL,
        Type.getInternalName(validatorClz),
        "<init>",
        "()V",
        false
    );

    mv.visitInsn(SWAP);     // ..., Collection, A, Collection, Validator, A

    mv.visitMethodInsn(
        INVOKEINTERFACE,
        "java/util/function/Function",
        "apply",
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        true
    );

    // ..., Collection, A, Collection, ValidationCollection
    mv.visitMethodInsn(
        INVOKEINTERFACE,
        "java/util/Collection",
        "addAll",
        "(Ljava/util/Collection;)Z",
        true
    );

    mv.visitInsn(POP);  // Discard result of addAll...
    mv.visitInsn(SWAP); // ..., A, Collection
  }
}
