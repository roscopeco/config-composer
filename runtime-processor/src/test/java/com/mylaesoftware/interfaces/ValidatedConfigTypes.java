package com.mylaesoftware.interfaces;

import com.mylaesoftware.annotations.ConfigType;
import com.mylaesoftware.annotations.ConfigValue;
import com.mylaesoftware.validators.ConfigValidator;
import com.mylaesoftware.validators.ValidationError;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ConfigType
public interface ValidatedConfigTypes {
  @ConfigValue(atPath = "test.string", validatedBy = PassesValidatorA.class)
  String stringWithSinglePassingValidator();

  @ConfigValue(atPath = "test.string", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  String stringWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.string", validatedBy = FailsValidatorA.class)
  String stringWithSingleFailingValidator();

  @ConfigValue(atPath = "test.string", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  String stringWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.string", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  String stringWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.boolean", validatedBy = PassesValidatorA.class)
  boolean boolWithSinglePassingValidator();

  @ConfigValue(atPath = "test.boolean", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  boolean boolWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.boolean", validatedBy = FailsValidatorA.class)
  boolean boolWithSingleFailingValidator();

  @ConfigValue(atPath = "test.boolean", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  boolean boolWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.boolean", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  boolean boolWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.byte", validatedBy = PassesValidatorA.class)
  byte byteWithSinglePassingValidator();

  @ConfigValue(atPath = "test.byte", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  byte byteWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.byte", validatedBy = FailsValidatorA.class)
  byte byteWithSingleFailingValidator();

  @ConfigValue(atPath = "test.byte", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  byte byteWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.byte", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  byte byteWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.char", validatedBy = PassesValidatorA.class)
  char charWithSinglePassingValidator();

  @ConfigValue(atPath = "test.char", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  char charWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.char", validatedBy = FailsValidatorA.class)
  char charWithSingleFailingValidator();

  @ConfigValue(atPath = "test.char", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  char charWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.char", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  char charWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.short", validatedBy = PassesValidatorA.class)
  short shortWithSinglePassingValidator();

  @ConfigValue(atPath = "test.short", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  short shortWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.short", validatedBy = FailsValidatorA.class)
  short shortWithSingleFailingValidator();

  @ConfigValue(atPath = "test.short", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  short shortWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.short", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  short shortWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.int", validatedBy = PassesValidatorA.class)
  int intWithSinglePassingValidator();

  @ConfigValue(atPath = "test.int", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  int intWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.int", validatedBy = FailsValidatorA.class)
  int intWithSingleFailingValidator();

  @ConfigValue(atPath = "test.int", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  int intWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.int", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  int intWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.long", validatedBy = PassesValidatorA.class)
  long longWithSinglePassingValidator();

  @ConfigValue(atPath = "test.long", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  long longWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.long", validatedBy = FailsValidatorA.class)
  long longWithSingleFailingValidator();

  @ConfigValue(atPath = "test.long", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  long longWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.long", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  long longWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.float", validatedBy = PassesValidatorA.class)
  float floatWithSinglePassingValidator();

  @ConfigValue(atPath = "test.float", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  float floatWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.float", validatedBy = FailsValidatorA.class)
  float floatWithSingleFailingValidator();

  @ConfigValue(atPath = "test.float", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  float floatWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.float", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  float floatWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.double", validatedBy = PassesValidatorA.class)
  double doubleWithSinglePassingValidator();

  @ConfigValue(atPath = "test.double", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  double doubleWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.double", validatedBy = FailsValidatorA.class)
  double doubleWithSingleFailingValidator();

  @ConfigValue(atPath = "test.double", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  double doubleWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.double", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  double doubleWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.lists.string", validatedBy = PassesValidatorA.class)
  List<String> listWithSinglePassingValidator();

  @ConfigValue(atPath = "test.lists.string", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  List<String> listWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.lists.string", validatedBy = FailsValidatorA.class)
  List<String> listWithSingleFailingValidator();

  @ConfigValue(atPath = "test.lists.string", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  List<String> listWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.lists.string", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  List<String> listWithOnePassingAndOneFailingValidator();

  @ConfigValue(atPath = "test.object", validatedBy = PassesValidatorA.class)
  Object anyWithSinglePassingValidator();

  @ConfigValue(atPath = "test.object", validatedBy = {PassesValidatorA.class, PassesValidatorB.class})
  Object anyWithMultiplePassingValidators();

  @ConfigValue(atPath = "test.object", validatedBy = FailsValidatorA.class)
  Object anyWithSingleFailingValidator();

  @ConfigValue(atPath = "test.object", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  Object anyWithMultipleFailingValidators();

  @ConfigValue(atPath = "test.object", validatedBy = {FailsValidatorA.class, FailsValidatorB.class})
  Object anyWithOnePassingAndOneFailingValidator();

  class PassesValidatorA implements ConfigValidator<Object> {
    @Override
    public Collection<ValidationError> apply(Object o) {
      return Collections.emptyList();
    }
  }

  class PassesValidatorB implements ConfigValidator<Object> {
    @Override
    public Collection<ValidationError> apply(Object o) {
      return Collections.emptyList();
    }
  }

  class FailsValidatorA implements ConfigValidator<Object> {
    @Override
    public Collection<ValidationError> apply(Object o) {
      return Collections.singletonList(new ValidationError("Failure A"));
    }
  }

  class FailsValidatorB implements ConfigValidator<Object> {
    @Override
    public Collection<ValidationError> apply(Object o) {
      return Collections.singletonList(new ValidationError("Failure B"));
    }
  }
}
