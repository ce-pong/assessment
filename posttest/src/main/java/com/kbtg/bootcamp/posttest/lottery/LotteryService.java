package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;

import com.kbtg.bootcamp.posttest.exception.LotteryRunOutException;
import com.kbtg.bootcamp.posttest.exception.NotFoundException;
import com.kbtg.bootcamp.posttest.user.User;
import com.kbtg.bootcamp.posttest.user.UserRepository;
import com.kbtg.bootcamp.posttest.user.UserTicket;
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
    public LotteryPurchaseResponse purchaseLottery(String userId, String ticketId){

        Optional<Lottery> optionalLottery = lotteryRepository.findById(ticketId);
        Lottery lottery;
        if(optionalLottery.isEmpty()){
            throw new NotFoundException("Ticket "+ticketId+" not found");
        }

        lottery = optionalLottery.get();

        if(lottery.getAmount() == 0){
            throw new LotteryRunOutException("Ticket "+ticketId+" has already been purchased");
        }

        UserTicket userTicket;
        try{
            User user;
            Optional<User> optionalUser = userRepository.findById(userId);
            if(optionalUser.isEmpty()){
                user = new User(userId);
                userRepository.save(user);
            }else{
                user = optionalUser.get();
            }

            // create purchase record
            userTicket = new UserTicket(lottery,user);
            userTicket = userTicketRepository.save(userTicket);

            // discount from storage
            lottery.setAmount(lottery.getAmount()-1);
            lotteryRepository.save(lottery);

        }catch (Exception ex){
            throw new InternalServerException("Failed to purchase lottery");
        }

        String recordId = Integer.toString(userTicket.getId());
        return new LotteryPurchaseResponse(recordId);
    }
}
