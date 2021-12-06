package com.mylaesoftware;

import com.typesafe.config.Config;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

class DefaultConfigComposerFactory<T> implements ConfigComposerFactory<T> {
  final Class<T> clz;

  DefaultConfigComposerFactory(Class<T> clz) {
    this.clz = clz;
  }

  @Override
  public T wire(Config config) {
    try {
      return clz.getDeclaredConstructor(Config.class)
          .newInstance(Objects.requireNonNull(config));
    } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
      throw new RuntimeException("[BUG] Generated class is bad", e);
    }
  }
}
