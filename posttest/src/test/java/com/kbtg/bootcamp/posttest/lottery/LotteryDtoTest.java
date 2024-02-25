package com.kbtg.bootcamp.posttest.lottery;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LotteryDtoTest {

    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validating a valid ticket should result in no constraint violations")
    void TicketValidation_ValidTicket_NoConstraintViolations()  {

        // Arrange
        String ticket = "000001";
        LotteryDto dto = new LotteryDto(ticket, 80, 1);

        // Act
        Set<ConstraintViolation<LotteryDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }
    @Test
    @DisplayName("Validating an empty ticket should result in two violations")
    void TicketValidation_EmptyTicket_ReturnsTwoViolations() {

        // Arrange
        String ticket = "";
        LotteryDto dto = new LotteryDto(ticket, 80, 1);

        // Act
        Set<ConstraintViolation<LotteryDto>> violations = validator.validate(dto);

        // Assert
        assertThat(violations).hasSize(2);

        List<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertThat(violationMessages).containsExactlyInAnyOrder("ticket must be exactly 6 digits","ticket should not empty");
    }

    @Test
    @DisplayName("Validating a ticket that is not 6 digits should return a violation")
    void TicketValidation_NonSixDigitTicket_ReturnsViolation() {

        // Arrange
        String ticket = "12345";
        LotteryDto dto = new LotteryDto(ticket, 80, 1);

        // Act
        Set<ConstraintViolation<LotteryDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty());

        Iterator<ConstraintViolation<LotteryDto>> iterator = violations.iterator();

        assertThat(iterator.next().getMessage()).isEqualTo("ticket must be exactly 6 digits");
    }






}