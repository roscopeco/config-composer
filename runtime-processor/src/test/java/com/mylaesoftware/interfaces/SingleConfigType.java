package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;

@ConfigType
public interface SingleConfigType {
  @ConfigValue(atPath = "test.string")
  String getTestPath();
}
