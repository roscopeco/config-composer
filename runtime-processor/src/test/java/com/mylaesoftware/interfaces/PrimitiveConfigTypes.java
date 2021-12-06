package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;

@ConfigType
public interface PrimitiveConfigTypes {
  @ConfigValue(atPath = "test.byte")
  byte byteConfig();

  @ConfigValue(atPath = "test.char")
  char charConfig();

  @ConfigValue(atPath = "test.short")
  short shortConfig();

  @ConfigValue(atPath = "test.int")
  int intConfig();

  @ConfigValue(atPath = "test.long")
  long longConfig();

  @ConfigValue(atPath = "test.float")
  float floatConfig();

  @ConfigValue(atPath = "test.double")
  double doubleConfig();

  @ConfigValue(atPath = "test.boolean")
  boolean booleanConfig();
}
