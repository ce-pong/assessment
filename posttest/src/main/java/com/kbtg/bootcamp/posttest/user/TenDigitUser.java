package com.kbtg.bootcamp.posttest.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TenDigitUserValidator.class)
public @interface TenDigitUser {
    String message() default "user must be exactly 10 digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}