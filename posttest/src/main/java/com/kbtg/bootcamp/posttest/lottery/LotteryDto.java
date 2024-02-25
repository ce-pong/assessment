package com.kbtg.bootcamp.posttest.lottery;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record LotteryDto(

        @NotEmpty(message = "ticket should not empty")
                @Pattern(regexp = "\\d{6}",message = "ticket must be exactly 6 digits")
        String ticket,

        @Positive(message = "input price more than 0")
        int price,

        @Positive(message = "input amount more than 0")
        int amount
) {
}
