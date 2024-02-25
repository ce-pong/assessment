package com.kbtg.bootcamp.posttest.lottery;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LotterySellBackResponse(
        @JsonProperty(value = "ticket")
        String ticketId
) {
}
