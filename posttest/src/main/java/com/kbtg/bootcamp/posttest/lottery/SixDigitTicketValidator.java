package com.kbtg.bootcamp.posttest.lottery;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SixDigitTicketValidator implements ConstraintValidator<SixDigitTicket, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("\\d{6}");
    }
}