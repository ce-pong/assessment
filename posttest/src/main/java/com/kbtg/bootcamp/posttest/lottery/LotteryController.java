package com.kbtg.bootcamp.posttest.lottery;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LotteryController {

    private LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService){
        this.lotteryService = lotteryService;
    }

    @GetMapping(value = "/lotteries")
    public ResponseEntity<LotteryListResponse> getAvailableLottery(){

        LotteryListResponse result = lotteryService.getAvailableTicketIds();

        return ResponseEntity.ok(result);
    }
}
