package com.kbtg.bootcamp.posttest.lottery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LotteryHistoryResponse(

        @JsonProperty(value = "tickets")
        List<String> tickets,
        @JsonProperty(value = "count")
        int countLottery,
        @JsonProperty(value = "cost")
        int totalPrice
) {
}
