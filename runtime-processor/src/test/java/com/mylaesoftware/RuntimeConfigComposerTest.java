package com.mylaesoftware;

import com.mylaesoftware.interfaces.BoxedPrimitiveConfigTypes;
import com.mylaesoftware.interfaces.CollectionsConfigTypes;
import com.mylaesoftware.interfaces.CustomMapperConfigType;
import com.mylaesoftware.interfaces.EmptyConfigType;
import com.mylaesoftware.interfaces.NonConfigType;
import com.mylaesoftware.interfaces.PrimitiveConfigTypes;
import com.mylaesoftware.interfaces.SingleConfigType;
import com.mylaesoftware.interfaces.ValidatedConfigTypes;
import com.mylaesoftware.validators.ConfigValidationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RuntimeConfigComposerTest {

  private static final List<String> TEST_STRING_LIST = Arrays.asList("one", "two", "three");
  private static final List<Duration> TEST_DURATION_LIST = Arrays.asList(Duration.ofDays(3), Duration.ofDays(2), Duration.ofDays(1));
  private static final List<Boolean> TEST_BOOL_LIST = Arrays.asList(false, true, true, false);
  private static final List<Integer> TEST_INT_LIST = Arrays.asList(1, 2, 3);
  private static final List<Long> TEST_LONG_LIST = Arrays.asList(10L, 9L, 8L, 7L);
  private static final List<Double> TEST_DOUBLE_LIST = Arrays.asList(32.0d, 42.0d);
  private static final List<Number> TEST_NUMBER_LIST = Arrays.asList(3, 4L, 5.0d);
  private static final List<Object> TEST_OBJECT_LIST = Arrays.asList("Anything", 3, 43.0);
  private static final String SOME_TEST_VALUE = "some-test-value";
  private static final Object TEST_OBJECT = "sneaky-string";


  @Test
  void testThrowsWhenNullClass() {
    assertThatThrownBy(() -> RuntimeConfigComposer.wire(null, emptyConfig()))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void testThrowsWhenNullConfig() {
    assertThatThrownBy(() -> RuntimeConfigComposer.wire(EmptyConfigType.class, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void testThrowsWhenNotAnInterface() {
    assertThatThrownBy(() -> RuntimeConfigComposer.wire(Object.class, emptyConfig()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void testThrowsWhenNotAConfigType() {
    assertThatThrownBy(() -> RuntimeConfigComposer.wire(NonConfigType.class, emptyConfig()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void testCanImplementEmptyConfigType() {
    RuntimeConfigComposer.wire(EmptyConfigType.class, emptyConfig());
  }

  @Test
  void testCanImplementSingleStringConfigType() {
    SingleConfigType sct = RuntimeConfigComposer.wire(SingleConfigType.class, testConfig());

    assertThat(sct.getTestPath()).isEqualTo(SOME_TEST_VALUE);
  }

  @Test
  void testCanImplementByteConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());

    assertThat(pct.byteConfig()).isEqualTo((byte) 0x7f);
  }

  @Test
  void testCanImplementCharConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());

    assertThat(pct.charConfig()).isEqualTo((char) 0x43);
  }

  @Test
  void testCanImplementShortConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());

    assertThat(pct.shortConfig()).isEqualTo((short) 32767);
  }

  @Test
  void testCanImplementIntConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());

    assertThat(pct.intConfig()).isEqualTo(123);
  }

  @Test
  void testCanImplementLongConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());

    assertThat(pct.longConfig()).isEqualTo(9001);
  }

  @Test
  void testCanImplementFloatConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());
    assertThat(pct.floatConfig()).isEqualTo(32.4f);
  }

  @Test
  void testCanImplementDoubleConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());
    assertThat(pct.doubleConfig()).isEqualTo(98.7);
  }

  @Test
  void testCanImplementBooleanConfigType() {
    PrimitiveConfigTypes pct = RuntimeConfigComposer.wire(PrimitiveConfigTypes.class, allConfig());
    assertThat(pct.booleanConfig()).isEqualTo(true);
  }

  @Test
  void testCanImplementBoxedByteConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());

    assertThat(pct.byteConfig()).isEqualTo((byte) 0x7f);
  }

  @Test
  void testCanImplementBoxedCharConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());

    assertThat(pct.charConfig()).isEqualTo((char) 0x43);
  }

  @Test
  void testCanImplementBoxedShortConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());

    assertThat(pct.shortConfig()).isEqualTo((short) 32767);
  }

  @Test
  void testCanImplementBoxedIntConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());

    assertThat(pct.intConfig()).isEqualTo(123);
  }

  @Test
  void testCanImplementBoxedLongConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());

    assertThat(pct.longConfig()).isEqualTo(9001);
  }

  @Test
  void testCanImplementBoxedFloatConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());
    assertThat(pct.floatConfig()).isEqualTo(32.4f);
  }

  @Test
  void testCanImplementBoxedDoubleConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());
    assertThat(pct.doubleConfig()).isEqualTo(98.7);
  }

  @Test
  void testCanImplementBoxedBooleanConfigType() {
    BoxedPrimitiveConfigTypes pct = RuntimeConfigComposer.wire(BoxedPrimitiveConfigTypes.class, allConfig());
    assertThat(pct.booleanConfig()).isEqualTo(true);
  }

  @Test
  void testCanUseCustomStringMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.uppercaseTestString()).isEqualTo("SOME-TEST-VALUE");
  }

  @Test
  void testCanUseCustomPrimitiveByteMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveByte()).isEqualTo((byte) 42);
  }

  @Test
  void testCanUseCustomBoxedByteMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedByte()).isEqualTo(Byte.valueOf((byte) 42));
  }

  @Test
  void testCanUseCustomPrimitiveCharMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveChar()).isEqualTo('*' /* ASCII 42 */);
  }

  @Test
  void testCanUseCustomBoxedCharMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedChar()).isEqualTo(Character.valueOf((char) 42));
  }

  @Test
  void testCanUseCustomPrimitiveShortMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveShort()).isEqualTo((short) 42);
  }

  @Test
  void testCanUseCustomBoxedShortMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedShort()).isEqualTo(Short.valueOf((short) 42));
  }

  @Test
  void testCanUseCustomPrimitiveIntMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveInt()).isEqualTo(42);
  }

  @Test
  void testCanUseCustomBoxedIntMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedInt()).isEqualTo(Integer.valueOf(42));
  }

  @Test
  void testCanUseCustomPrimitiveLongMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveLong()).isEqualTo(42L);
  }

  @Test
  void testCanUseCustomBoxedLongMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedLong()).isEqualTo(Long.valueOf(42L));
  }

  @Test
  void testCanUseCustomPrimitiveFloatMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveFloat()).isEqualTo(42.0f);
  }

  @Test
  void testCanUseCustomBoxedFloatMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedFloat()).isEqualTo(Float.valueOf(42.0f));
  }

  @Test
  void testCanUseCustomPrimitiveDoubleMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveDouble()).isEqualTo(42.0d);
  }

  @Test
  void testCanUseCustomBoxedDoubleMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedDouble()).isEqualTo(Double.valueOf(42.0d));
  }

  @Test
  void testCanUseCustomPrimitiveBoolMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customPrimitiveBoolean()).isEqualTo(false);
  }

  @Test
  void testCanUseCustomBoxedBoolMapper() {
    CustomMapperConfigType cmct = RuntimeConfigComposer.wire(CustomMapperConfigType.class, testConfig());
    assertThat(cmct.customBoxedBoolean()).isEqualTo(Boolean.valueOf(false));
  }

  @Test
  void testCanImplementStringListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.stringList()).hasSize(3);
    assertThat(cct.stringList()).hasSameElementsAs(TEST_STRING_LIST);
  }

  @Test
  void testCanImplementDurationListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.durationList()).hasSize(3);
    assertThat(cct.durationList()).hasSameElementsAs(TEST_DURATION_LIST);
  }

  @Test
  void testCanImplementBooleanListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.boolList()).hasSize(4);
    assertThat(cct.boolList()).hasSameElementsAs(TEST_BOOL_LIST);
  }

  @Test
  void testCanImplementIntListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.intList()).hasSize(3);
    assertThat(cct.intList()).hasSameElementsAs(TEST_INT_LIST);
  }

  @Test
  void testCanImplementLongListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.longList()).hasSize(4);
    assertThat(cct.longList()).hasSameElementsAs(TEST_LONG_LIST);
  }

  @Test
  void testCanImplementDoubleListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.doubleList()).hasSize(2);
    assertThat(cct.doubleList()).hasSameElementsAs(TEST_DOUBLE_LIST);
  }

  @Test
  void testCanImplementNumberListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.numberList()).hasSize(3);
    assertThat(cct.numberList()).hasSameElementsAs(TEST_NUMBER_LIST);
  }

  @Test
  void testCanImplementAnyListConfigType() {
    CollectionsConfigTypes cct = RuntimeConfigComposer.wire(CollectionsConfigTypes.class, allConfig());
    assertThat(cct.anyList()).hasSize(3);
    assertThat(cct.anyList()).hasSameElementsAs(TEST_OBJECT_LIST);
  }

  @Test
  void testSinglePassingValidatorSucceedsForString() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::stringWithSinglePassingValidator, "some-test-value");
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForString() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::stringWithMultiplePassingValidators, "some-test-value");
  }

  @Test
  void testSingleFailingValidatorFailsForString() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::stringWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForString() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::stringWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForString() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::stringWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForBool() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::boolWithSinglePassingValidator, true);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForBool() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::boolWithMultiplePassingValidators, true);
  }

  @Test
  void testSingleFailingValidatorFailsForBool() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::boolWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForBool() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::boolWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForBool() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::boolWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForByte() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::byteWithSinglePassingValidator, (byte) 127);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForByte() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::byteWithMultiplePassingValidators, (byte) 127);
  }

  @Test
  void testSingleFailingValidatorFailsForByte() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::byteWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForByte() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::byteWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForByte() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::byteWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForChar() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::charWithSinglePassingValidator, (char) 0x43);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForChar() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::charWithMultiplePassingValidators, (char) 0x43);
  }

  @Test
  void testSingleFailingValidatorFailsForChar() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::charWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForChar() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::charWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForChar() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::charWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForShort() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::shortWithSinglePassingValidator, (short) 32767);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForShort() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::shortWithMultiplePassingValidators, (short) 32767);
  }

  @Test
  void testSingleFailingValidatorFailsForShort() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::shortWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForShort() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::shortWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForShort() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::shortWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForInt() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::intWithSinglePassingValidator, 123);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForInt() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::intWithMultiplePassingValidators, 123);
  }

  @Test
  void testSingleFailingValidatorFailsForInt() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::intWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForInt() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::intWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForInt() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::intWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForLong() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::longWithSinglePassingValidator, 9001L);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForLong() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::longWithMultiplePassingValidators, 9001L);
  }

  @Test
  void testSingleFailingValidatorFailsForLong() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::longWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForLong() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::longWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForLong() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::longWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForFloat() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::floatWithSinglePassingValidator, 32.4f);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForFloat() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::floatWithMultiplePassingValidators, 32.4f);
  }

  @Test
  void testSingleFailingValidatorFailsForFloat() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::floatWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForFloat() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::floatWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForFloat() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::floatWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForDouble() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::doubleWithSinglePassingValidator, 98.7);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForDouble() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::doubleWithMultiplePassingValidators, 98.7);
  }

  @Test
  void testSingleFailingValidatorFailsForDouble() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::doubleWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForDouble() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::doubleWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForDouble() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::doubleWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForList() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::listWithSinglePassingValidator, TEST_STRING_LIST);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForList() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::listWithMultiplePassingValidators, TEST_STRING_LIST);
  }

  @Test
  void testSingleFailingValidatorFailsForList() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::listWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForList() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::listWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForList() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::listWithOnePassingAndOneFailingValidator);
  }

  @Test
  void testSinglePassingValidatorSucceedsForAny() {
    checkSinglePassingValidatorSucceeds(ValidatedConfigTypes::anyWithSinglePassingValidator, TEST_OBJECT);
  }

  @Test
  void testMultiplePassingValidatorsSucceedsForAny() {
    checkMultiplePassingValidatorsSucceeds(ValidatedConfigTypes::anyWithMultiplePassingValidators, TEST_OBJECT);
  }

  @Test
  void testSingleFailingValidatorFailsForAny() {
    checkSingleFailingValidatorFails(ValidatedConfigTypes::anyWithSingleFailingValidator);
  }

  @Test
  void testMultipleFailingValidatorsFailsForAny() {
    checkMultipleFailingValidatorsFails(ValidatedConfigTypes::anyWithMultipleFailingValidators);
  }

  @Test
  void testSinglePassingAndSingleFailingValidatorFailsForAny() {
    checkSinglePassingAndSingleFailingValidatorFails(
        ValidatedConfigTypes::anyWithOnePassingAndOneFailingValidator);
  }


  <T> void checkSinglePassingValidatorSucceeds(
      final Function<ValidatedConfigTypes, T> methodUnderTest,
      final T expectedValue
  ) {
    final ValidatedConfigTypes vct = RuntimeConfigComposer.wire(ValidatedConfigTypes.class, allConfig());
    assertThat(methodUnderTest.apply(vct)).isEqualTo(expectedValue);
  }

  <T> void checkMultiplePassingValidatorsSucceeds(
      final Function<ValidatedConfigTypes, T> methodUnderTest,
      final T expectedValue
  ) {
    final ValidatedConfigTypes vct = RuntimeConfigComposer.wire(ValidatedConfigTypes.class, allConfig());
    assertThat(methodUnderTest.apply(vct)).isEqualTo(expectedValue);
  }

  void checkSingleFailingValidatorFails(final Function<ValidatedConfigTypes, ?> methodUnderTest) {
    final ValidatedConfigTypes vct = RuntimeConfigComposer.wire(ValidatedConfigTypes.class, allConfig());

    assertThatThrownBy(() -> methodUnderTest.apply(vct))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContaining("Failure A");
  }

  void checkMultipleFailingValidatorsFails(final Function<ValidatedConfigTypes, ?> methodUnderTest) {
    final ValidatedConfigTypes vct = RuntimeConfigComposer.wire(ValidatedConfigTypes.class, allConfig());

    assertThatThrownBy(() -> methodUnderTest.apply(vct))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContaining("Failure A")
        .hasMessageContaining("Failure B");
  }

  void checkSinglePassingAndSingleFailingValidatorFails(final Function<ValidatedConfigTypes, ?> methodUnderTest) {
    final ValidatedConfigTypes vct = RuntimeConfigComposer.wire(ValidatedConfigTypes.class, allConfig());

    assertThatThrownBy(() -> methodUnderTest.apply(vct))
        .isInstanceOf(ConfigValidationException.class)
        .hasMessageContaining("Failure A");
  }

  private Config emptyConfig() {
    return ConfigFactory.empty();
  }

  private Config testConfig() {
    final Map<String, String> config = new HashMap<>();
    config.put("test.string", SOME_TEST_VALUE);
    return ConfigFactory.parseMap(config);
  }

  private Config allConfig() {
    final Map<String, Object> config = new HashMap<>();
    config.put("test.string", SOME_TEST_VALUE);
    config.put("test.byte", 0x7f);
    config.put("test.char", 0x43);
    config.put("test.short", 32767);
    config.put("test.int", 123);
    config.put("test.long", 9001L);
    config.put("test.float", 32.4f);
    config.put("test.double", 98.7d);
    config.put("test.boolean", true);
    config.put("test.object", TEST_OBJECT);
    config.put("test.lists.string", TEST_STRING_LIST);
    config.put("test.lists.duration", TEST_DURATION_LIST);
    config.put("test.lists.bool", TEST_BOOL_LIST);
    config.put("test.lists.int", TEST_INT_LIST);
    config.put("test.lists.long", TEST_LONG_LIST);
    config.put("test.lists.double", TEST_DOUBLE_LIST);
    config.put("test.lists.number", TEST_NUMBER_LIST);
    //config.put("tests.lists.config", TEST_CONFIG_LIST)
    config.put("test.lists.any", TEST_OBJECT_LIST);
    return ConfigFactory.parseMap(config);
  }
}
