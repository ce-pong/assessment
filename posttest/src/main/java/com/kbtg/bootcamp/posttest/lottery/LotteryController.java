package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.user.TenDigitUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class LotteryController {

    private LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService){
        this.lotteryService = lotteryService;
    }

    @GetMapping(value = "/lotteries")
    @ResponseStatus(value = HttpStatus.OK)
    public LotteryListResponse getAvailableLottery(){

        return lotteryService.getAvailableTicketIds();
    }

    @PostMapping(value = "/admin/lotteries")
    @ResponseStatus(HttpStatus.CREATED)
    public LotteryResponse createLottery(@RequestBody @Validated LotteryDto request){

        return  lotteryService.createLottery(request);
    }

    @PostMapping(value = "/users/{userId}/lotteries/{ticketId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LotteryPurchaseReponse purchaseTicket(
            @PathVariable(value = "userId") @TenDigitUser String userId,
            @PathVariable(value = "ticketId") @SixDigitTicket String ticketId){

        return lotteryService.purchaseLottery(userId,ticketId);
    }
}
