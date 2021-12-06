package com.mylaesoftware.runtime;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

abstract class UnsafeDefineClassStrategy implements DefineClassStrategy {
  private static final Unsafe unsafe;

  static {
    try {
      final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      unsafe = (Unsafe) theUnsafe.get(null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Unsafe isn't working as it should");
    }
  }

  protected Unsafe getUnsafe() {
    return unsafe;
  }

  /**
   * Requires Java8 to compile. Subclass, and implement this method with:
   *
   * <code>return (Class&lt;T&gt;)getUnsafe().defineClass(code);</code>
   */
  @Override
  public abstract <T> Class<? extends T> defineClass(Class<T> type, byte[] code);
}
