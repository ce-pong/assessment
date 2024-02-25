package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;

import com.kbtg.bootcamp.posttest.user.User;
import com.kbtg.bootcamp.posttest.user.UserRepository;
import com.kbtg.bootcamp.posttest.user.UserTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LotteryService {
    private final LotteryRepository lotteryRepository;

    private final UserTicketRepository userTicketRepository;

    private final UserRepository userRepository;

    public LotteryService(LotteryRepository lotteryRepository, UserTicketRepository userTicketRepository, UserRepository userRepository) {
        this.lotteryRepository = lotteryRepository;
        this.userTicketRepository = userTicketRepository;
        this.userRepository = userRepository;
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

    @Transactional
    public LotteryPurchaseReponse purchaseLottery(String userId,String ticketId){

        return null;
    }
}
