package com.ibm.ws.jsf22.fat.PI64718;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ListSizeValidation implements ConstraintValidator<ListSizeValidator, List<String>> {

	@Override
	public void initialize(ListSizeValidator constraintAnnotation) {
	}

	@Override
	public boolean isValid(List<String> value, ConstraintValidatorContext context) {
		return value != null && value.size() == 2;
	}

}
