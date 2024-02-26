package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.user.TenDigitUser;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService){
        this.lotteryService = lotteryService;
    }

    // EXP01
    @PostMapping(value = "/admin/lotteries")
    @ResponseStatus(HttpStatus.CREATED)
    public LotteryResponse createLottery(@RequestBody @Validated LotteryDto request){

        return  lotteryService.addLottery(request);
    }

    // EXP02
    @GetMapping(value = "/lotteries")
    @ResponseStatus(value = HttpStatus.OK)
    public LotteryListResponse getAvailableLottery(){

        return lotteryService.getAvailableTicketIds();
    }

    // EXP03
    @PostMapping(value = "/users/{userId}/lotteries/{ticketId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LotteryPurchaseResponse purchaseLottery(
            @PathVariable(value = "userId") @TenDigitUser String userId,
            @PathVariable(value = "ticketId") @SixDigitTicket String ticketId){

        return lotteryService.purchaseLottery(userId,ticketId);
    }


    // EXP04
    @GetMapping(value = "/users/{userId}/lotteries")
    @ResponseStatus(HttpStatus.OK)
    public LotteryHistoryResponse getPurchaseHistory(
            @PathVariable(value = "userId") @TenDigitUser String userId){

        return lotteryService.getPurchaseHistory(userId);
    }

    // EXP05
    @DeleteMapping(value = "/users/{userId}/lotteries/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    public LotterySellBackResponse sellBackLottery(
            @PathVariable(value = "userId") @TenDigitUser String userId,
            @PathVariable(value = "ticketId") @SixDigitTicket String ticketId){

        return lotteryService.sellBackLottery(userId,ticketId);
    }


}
