package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;
import com.mylaesoftware.mappers.ConfigMapper;
import com.typesafe.config.Config;

@ConfigType
public interface CustomMapperConfigType {
  final class CustomStringMapper implements ConfigMapper<String> {
    @Override
    public String apply(Config config, String s) {
      return config.getString(s).toUpperCase();
    }
  }

  final class CustomIntMapper implements ConfigMapper<Integer> {
    @Override
    public Integer apply(Config config, String s) {
      return 42;
    }
  }

  final class CustomLongMapper implements ConfigMapper<Long> {
    @Override
    public Long apply(Config config, String s) {
      return 42L;
    }
  }

  final class CustomDoubleMapper implements ConfigMapper<Double> {
    @Override
    public Double apply(Config config, String s) {
      return 42D;
    }
  }

  final class CustomBoolMapper implements ConfigMapper<Boolean> {
    @Override
    public Boolean apply(Config config, String s) {
      return false;
    }
  }


  @ConfigValue(atPath = "test.string", mappedBy = CustomStringMapper.class)
  String uppercaseTestString();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  Byte customBoxedByte();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  byte customPrimitiveByte();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  Character customBoxedChar();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  char customPrimitiveChar();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  Short customBoxedShort();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  short customPrimitiveShort();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  Integer customBoxedInt();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomIntMapper.class)
  int customPrimitiveInt();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomLongMapper.class)
  Long customBoxedLong();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomLongMapper.class)
  long customPrimitiveLong();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomDoubleMapper.class)
  Float customBoxedFloat();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomDoubleMapper.class)
  float customPrimitiveFloat();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomDoubleMapper.class)
  Double customBoxedDouble();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomDoubleMapper.class)
  double customPrimitiveDouble();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomBoolMapper.class)
  Boolean customBoxedBoolean();

  @ConfigValue(atPath = "doesnt.exist", mappedBy = CustomBoolMapper.class)
  boolean customPrimitiveBoolean();
}
