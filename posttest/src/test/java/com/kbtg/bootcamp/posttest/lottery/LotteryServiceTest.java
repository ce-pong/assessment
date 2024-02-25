package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import com.kbtg.bootcamp.posttest.user.User;
import com.kbtg.bootcamp.posttest.user.UserRepository;
import com.kbtg.bootcamp.posttest.user.UserTicket;
import com.kbtg.bootcamp.posttest.user.UserTicketRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;


import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LotteryServiceTest {

    @Mock
    private LotteryRepository lotteryRepository;

    @Mock
    private UserTicketRepository userTicketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LotteryService lotteryService;

    @Test
    @DisplayName("Get Available Ticket IDs With No Data Should Return Empty List Response")
    void whenGetAvailableTicket_withNoData_ShouldReturnResponseWithEmptyList() {
        // Arrange
        when(lotteryRepository.findByAmountMoreThanZero()).thenReturn(Collections.emptyList());

        // Act
        LotteryListResponse actual = lotteryService.getAvailableTicketIds();

        // Assert
        List<String> expected = List.of();
        assertEquals(expected, actual.getTickets());
    }

    @Test
    @DisplayName("Get Available Ticket IDs With Data Should Return Correctly List Of Ticket Id")
    void gwhenGetAvailableTicket_withData_ShouldReturnResponseWithListStringTicketId() {
        // Arrange
        Lottery lottery1 = new Lottery();
        lottery1.setTicketId("000001");
        lottery1.setAmount(1);

        Lottery lottery2 = new Lottery();
        lottery2.setTicketId("000002");
        lottery2.setAmount(1);

        Lottery lottery3 = new Lottery();
        lottery3.setTicketId("123456");
        lottery3.setAmount(1);

        when(lotteryRepository.findByAmountMoreThanZero()).thenReturn(List.of(lottery1,lottery2,lottery3));

        // Act
        LotteryListResponse actual = lotteryService.getAvailableTicketIds();

        // Assert
        List<String> expectedTicketIds = List.of("000001", "000002", "123456");
        assertEquals(expectedTicketIds, actual.getTickets());

    }

    @Test
    @DisplayName("Handling Internal Server Error When Retrieving Available Tickets")
    void whenRetrievingTicketsFails_ShouldThrowInternalServerException() {
        // Arrange
        when(lotteryRepository.findByAmountMoreThanZero()).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(InternalServerException.class, () -> {lotteryService.getAvailableTicketIds();});
    }

    @Test
    @DisplayName("Add lottery should return lottery response")
    void whenCreateLottery_ShouldReturnLotteryResponseWithTicket(){
        // Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        Lottery lottery = new Lottery("000001",80,1);
        when(lotteryRepository.save(any(Lottery.class))).thenReturn(lottery);

        // Act
        LotteryResponse actual = lotteryService.createLottery(request);

        // Assert
        assertEquals("000001",actual.ticket());
    }

    @Test
    @DisplayName("Handling Internal Server Error When Create Lottery")
    void whenCreateLotteryFails_ShouldThrowInternalServerException() {
        // Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        when(lotteryRepository.save(any(Lottery.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(InternalServerException.class, () -> {lotteryService.createLottery(request);});
    }

    @Test
    @DisplayName("Purchase lottery should be return id from User_ticket")
    void whenPurchaseLottery_ShouldReturnLotteryPurchaseResponseWithUserTicketId(){

        // Arrange
        String userId = "0123456789";
        User user = new User(userId);
        String ticket = "000001";
        Lottery lottery = new Lottery(ticket,80,1);
        UserTicket userTicket = new UserTicket();
        userTicket.setId(1);
        userTicket.setUser(user);
        userTicket.setLottery(lottery);

        when(userTicketRepository.save(any(UserTicket.class))).thenReturn(userTicket);

        // Act
        LotteryPurchaseReponse actual = lotteryService.purchaseLottery(userId,ticket);

        // Assert
        int expected = 1;
        assertEquals(expected,actual.id());
    }
}