package com.denmit.userbalance.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueElementsInSetValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueElementsInSet {

    String message() default "Set contains duplicate values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}