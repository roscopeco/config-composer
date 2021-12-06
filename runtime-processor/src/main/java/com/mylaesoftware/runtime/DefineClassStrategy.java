package com.mylaesoftware.runtime;

public interface DefineClassStrategy {
  <T> Class<? extends T> defineClass(Class<T> type, byte[] code);
}
