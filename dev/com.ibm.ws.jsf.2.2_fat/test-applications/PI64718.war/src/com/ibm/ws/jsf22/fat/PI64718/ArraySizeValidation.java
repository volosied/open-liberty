package com.ibm.ws.jsf22.fat.PI64718;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ArraySizeValidation implements ConstraintValidator<ArraySizeValidator, String[]> {

	@Override
	public void initialize(ArraySizeValidator constraintAnnotation) {
	}

	@Override
	public boolean isValid(String[] value, ConstraintValidatorContext context) {
		return value != null && value.length == 2;
	}

}
