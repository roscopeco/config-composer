package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;
import com.typesafe.config.Config;

import java.time.Duration;
import java.util.List;

@ConfigType
public interface CollectionsConfigTypes {

  @ConfigValue(atPath = "test.lists.string")
  List<String> stringList();

  @ConfigValue(atPath = "test.lists.duration")
  List<Duration> durationList();

  @ConfigValue(atPath = "test.lists.bool")
  List<Boolean> boolList();

  @ConfigValue(atPath = "test.lists.int")
  List<Integer> intList();

  @ConfigValue(atPath = "test.lists.long")
  List<Long> longList();

  @ConfigValue(atPath = "test.lists.double")
  List<Double> doubleList();

  @ConfigValue(atPath = "test.lists.number")
  List<Number> numberList();

  @ConfigValue(atPath = "test.lists.config")
  List<Config> configList();

  @ConfigValue(atPath = "test.lists.any")
  List<Object> anyList();
}
