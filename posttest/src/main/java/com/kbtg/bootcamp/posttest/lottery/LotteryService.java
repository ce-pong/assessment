package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class LotteryService {
    private final LotteryRepository lotteryRepository;

    public LotteryService(LotteryRepository lotteryRepository){
        this.lotteryRepository = lotteryRepository;
    }

    public Boolean checkValidTicket(String ticket){
        return true;
    }
    public LotteryListResponse getAvailableTicketIds() {
        try{
            List<Lottery> tickets = lotteryRepository.findByAmountMoreThanZero();

            List<String> ticketIds =  tickets.stream()
                    .map(Lottery::getTicketId)
                    .collect(Collectors.toList());

            return new LotteryListResponse(ticketIds);
        }catch(Exception ex){
            throw new InternalServerException("Failed to get available ticket");
        }
    }

    public LotteryResponse createLottery(LotteryDto request){
        try{
            Lottery lottery = new Lottery();
            lottery.setTicketId(request.ticket());
            lottery.setPrice(request.price());
            lottery.setAmount(request.amount());

            lotteryRepository.save(lottery);

            return new LotteryResponse(lottery.getTicketId());
        }catch (Exception ex){
            throw new InternalServerException("Failed to get create lottery");
        }

    }

}
