package com.kbtg.bootcamp.posttest.lottery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LotteryListResponse {

    @JsonProperty(value = "tickets")
    private List<String> tickets;

    public LotteryListResponse(List<String> tickets) {
        this.tickets = tickets;
    }

    public List<String> getTickets() {
        return tickets;
    }

}
