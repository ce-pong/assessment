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
    @DisplayName("when ticket is valid then no return violation")
    void whenTicketIsValid_thenNoConstraintViolations() {

        // Arrange
        String ticket = "000001";
        LotteryDto dto = new LotteryDto(ticket, 80, 1);

        // Act
        Set<ConstraintViolation<LotteryDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }
    @Test
    @DisplayName("when ticket is empty then return 2 violation")
    void whenTicketIsEmpty_thenConstraintViolation() {

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
    @DisplayName("when ticket is not 6 digit then return violation")
    void whenTicketIsNot6Digits_thenConstraintViolation() {

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