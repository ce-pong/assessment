package com.kbtg.bootcamp.posttest.lottery;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/admin/lotteries")
    public ResponseEntity<LotteryResponse> createLottery(@RequestBody
                                                          @Validated
                                                          LotteryDto request){

        LotteryResponse result = lotteryService.createLottery(request);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
