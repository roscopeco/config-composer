package com.mylaesoftware.runtime;

import com.mylaesoftware.RuntimeConfigComposer;

import java.lang.invoke.MethodHandles;

public class LookupDefineClassStrategy implements DefineClassStrategy {
  private static final MethodHandles.Lookup lookup;

  static {
    try {
      lookup = MethodHandles.privateLookupIn(RuntimeConfigComposer.class, MethodHandles.lookup());
    } catch (IllegalAccessException e) {
      throw new UnsupportedOperationException(
          "Cannot get private lookup for our own classes - likely a misconfiguration");
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Class<? extends T> defineClass(Class<T> type, byte[] code) {
    try {
      return (Class<T>) lookup.defineClass(code);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
