package com.mylaesoftware;

import com.typesafe.config.Config;

public interface ConfigComposerFactory<T> {
  T wire(Config config);
}
