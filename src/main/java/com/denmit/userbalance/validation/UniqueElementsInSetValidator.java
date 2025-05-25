package com.denmit.userbalance.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UniqueElementsInSetValidator implements ConstraintValidator<UniqueElementsInSet, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (value == null) return true;

        Set<Object> unique = new HashSet<>();
        for (Object element : value) {
            if (!unique.add(element)) {
                return false;
            }
        }
        return true;
    }
}
