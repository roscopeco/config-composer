package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;

@ConfigType
public interface BoxedPrimitiveConfigTypes {
  @ConfigValue(atPath = "test.byte")
  Byte byteConfig();

  @ConfigValue(atPath = "test.char")
  Character charConfig();

  @ConfigValue(atPath = "test.short")
  Short shortConfig();

  @ConfigValue(atPath = "test.int")
  Integer intConfig();

  @ConfigValue(atPath = "test.long")
  Long longConfig();

  @ConfigValue(atPath = "test.float")
  Float floatConfig();

  @ConfigValue(atPath = "test.double")
  Double doubleConfig();

  @ConfigValue(atPath = "test.boolean")
  Boolean booleanConfig();
}
