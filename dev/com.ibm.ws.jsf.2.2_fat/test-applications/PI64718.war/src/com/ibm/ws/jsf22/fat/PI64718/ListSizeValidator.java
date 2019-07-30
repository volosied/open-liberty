package com.ibm.ws.jsf22.fat.PI64718;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ListSizeValidation.class)
public @interface ListSizeValidator {

	String message() default "ListSize is not 2";

	Class<?>[] groups() default {};

	Class<? extends ListSizeValidator>[] payload() default {};
}
