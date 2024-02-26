package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import com.kbtg.bootcamp.posttest.exception.LotteryRunOutException;
import com.kbtg.bootcamp.posttest.exception.NotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    //region EXP01
    @Test
    @DisplayName("Add lottery should return a lottery response with ticket")
    void addLottery_ReturnsLotteryResponseWithTicket(){
        //Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        Lottery lottery = new Lottery("000001",80,1);
        when(lotteryRepository.save(any(Lottery.class))).thenReturn(lottery);

        //Act
        LotteryResponse actual = lotteryService.addLottery(request);

        //Assert
        assertEquals("000001",actual.ticket());
    }

    @Test
    @DisplayName("Handling internal server error when adding a lottery throws InternalServerException")
    void addLottery_Fails_ThrowsInternalServerException() {
        //Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        when(lotteryRepository.save(any(Lottery.class))).thenThrow(new RuntimeException("Database connection error"));

        //Act & Assert
        assertThrows(InternalServerException.class, () -> lotteryService.addLottery(request));
    }
    //endregion

    //region EXP02
    @Test
    @DisplayName("Get Available Ticket IDs when there is no data should return an empty list response")
    void getAvailableTicketIds_WithNoData_ReturnsEmptyListResponse(){
        //Arrange
        when(lotteryRepository.findByAmountMoreThanZero()).thenReturn(Collections.emptyList());

        //Act
        LotteryListResponse actual = lotteryService.getAvailableTicketIds();

        //Assert
        List<String> expected = List.of();
        assertEquals(expected, actual.getTickets());
    }

    @Test
    @DisplayName("Get Available Ticket IDs with data should return a correctly ordered list of ticket IDs in ascending order")
    void getAvailableTicketIds_WithData_ReturnsListTicketIdAscending() {
        //Arrange
        Lottery lottery1 = new Lottery();
        lottery1.setTicketId("000002");
        lottery1.setAmount(1);

        Lottery lottery2 = new Lottery();
        lottery2.setTicketId("123456");
        lottery2.setAmount(1);

        Lottery lottery3 = new Lottery();
        lottery3.setTicketId("000001");
        lottery3.setAmount(1);

        when(lotteryRepository.findByAmountMoreThanZero()).thenReturn(List.of(lottery1,lottery2,lottery3));

        //Act
        LotteryListResponse actual = lotteryService.getAvailableTicketIds();

        //Assert
        List<String> expectedTicketIds = List.of("000001", "000002", "123456");
        assertEquals(expectedTicketIds, actual.getTickets());

    }

    @Test
    @DisplayName("Handling internal server error when get available tickets throws InternalServerException")
    void getAvailableTicketIds_Fails_ThrowsInternalServerException()  {
        //Arrange
        doThrow(new RuntimeException("Database connection error")).when(lotteryRepository).findByAmountMoreThanZero();

        //Act & Assert
        assertThrows(InternalServerException.class, () -> lotteryService.getAvailableTicketIds());
    }
    //endregion

    //region EXP03
    @Test
    @DisplayName("Purchase lottery throws NotFoundException if ticket is not found")
    void purchaseLottery_NoTicket_ThrowsNotFoundException(){

        //Arrange
        String ticket = "000001";
        String userId = "0123456789";

        when(lotteryRepository.findById(ticket)).thenReturn(Optional.empty());


        //Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> lotteryService.purchaseLottery(userId,ticket));

        String actualMessage = exception.getMessage();
        String expectedMessage = "Ticket "+ticket+" not found";

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    @DisplayName("Purchase lottery throws LotteryRunOutException if ticket is already purchased")
    void purchaseLottery_TicketRunOut_ThrowsLotteryRunOutException(){

        // Arrange
        String ticket = "000001";
        String userId = "0123456789";
        Lottery lottery = new Lottery(ticket,80,0);

        when(lotteryRepository.findById(ticket)).thenReturn(Optional.of(lottery));


        // Act & Assert
        Exception exception = assertThrows(LotteryRunOutException.class, () -> lotteryService.purchaseLottery(userId,ticket));

        String actualMessage = exception.getMessage();
        String expectedMessage = "Ticket "+ticket+" has already been purchased";

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    @DisplayName("Purchase lottery with database error should throw InternalServerException")
    void purchaseLottery_Failed_ThrowsInternalServerException(){

        // Arrange
        String ticket = "000001";
        String userId = "0123456789";
        Lottery lottery = new Lottery(ticket,80,1);

        when(lotteryRepository.findById(ticket)).thenReturn(Optional.of(lottery));
        doThrow(new RuntimeException("Database connection error")).when(userTicketRepository).save(any(UserTicket.class));

        // Act & Assert
        Exception exception = assertThrows(InternalServerException.class, () -> lotteryService.purchaseLottery(userId,ticket));

        String actualMessage = exception.getMessage();
        String expectedMessage = "Failed to purchase lottery";

        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    @DisplayName("Purchase lottery with existing user returns LotteryPurchaseResponse with UserTicketId")
    void purchaseLottery_withExistingUser_ReturnsResponseWithUserTicketId(){

        // Arrange
        String ticketId = "000001";
        String userId = "0123456789";
        Lottery lottery = new Lottery(ticketId,80,1);
        User user = new User(userId);
        UserTicket newUserTicket = new UserTicket(lottery,user);
        newUserTicket.setId(1);

        when(lotteryRepository.findById(ticketId)).thenReturn(Optional.of(lottery));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userTicketRepository.save(any(UserTicket.class))).thenReturn(newUserTicket);

        // Act
        LotteryPurchaseResponse actual = lotteryService.purchaseLottery(userId,ticketId);

        // Assert
        String expected = Integer.toString(newUserTicket.getId());
        assertEquals(expected,actual.id());
    }

    @Test
    @DisplayName("Purchase lottery with no existing user returns LotteryPurchaseResponse with UserTicketId")
    void purchaseLottery_withNoExistingUser_ReturnsResponseWithUserTicketId(){

        // Arrange
        String ticketId = "000001";
        String userId = "0123456789";
        Lottery lottery = new Lottery(ticketId, 80, 1);
        User newUser = new User(userId);
        UserTicket newUserTicket = new UserTicket(lottery, newUser);
        newUserTicket.setId(1);

        when(lotteryRepository.findById(ticketId)).thenReturn(Optional.of(lottery));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(userTicketRepository.save(any(UserTicket.class))).thenReturn(newUserTicket);

        // Act
        LotteryPurchaseResponse actual = lotteryService.purchaseLottery(userId,ticketId);

        // Assert
        String expected = Integer.toString(newUserTicket.getId());
        assertEquals(expected,actual.id());

        verify(userRepository, times(1)).save(any(User.class));
    }
    //endregion

    //region EXP04
    @Test
    @DisplayName("Get purchase history with no data should return correct response")
    public void getPurchaseHistory_NoData_ReturnsCorrectResponse(){

        // Arrange
        String userId = "0123456789";

        when(userTicketRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        LotteryHistoryResponse actual = lotteryService.getPurchaseHistory(userId);

        // Assert
        List<String> expectedTicketIds = List.of();
        assertEquals(expectedTicketIds, actual.tickets());

        int expectedCountLottery = 0;
        assertEquals(expectedCountLottery,actual.countLottery());

        int expectTotalPrice = 0;
        assertEquals(expectTotalPrice,actual.totalPrice());

    }

    @Test
    @DisplayName("Get purchase history should return correct response")
    public void getPurchaseHistory_ReturnsCorrectResponse(){

        // Arrange
        String userId = "0123456789";
        User user = new User(userId);

        Lottery lottery1 = new Lottery("123456",100,1);
        Lottery lottery2 = new Lottery("000002",80,1);
        UserTicket userTicket1 = new UserTicket(lottery1,user);
        userTicket1.setId(1);
        UserTicket userTicket2 = new UserTicket(lottery2,user);
        userTicket2.setId(2);
        when(userTicketRepository.findByUserId(userId)).thenReturn(List.of(userTicket1,userTicket2));

        // Act
        LotteryHistoryResponse actual = lotteryService.getPurchaseHistory(userId);

        // Assert
        List<String> expectedTicketIds = List.of( "000002", "123456");
        assertEquals(expectedTicketIds, actual.tickets());

        int expectedCountLottery = 2;
        assertEquals(expectedCountLottery,actual.countLottery());

        int expectTotalPrice = 180;
        assertEquals(expectTotalPrice,actual.totalPrice());

    }
    //endregion

    //region EXP05
    @Test
    @DisplayName("Sell back lottery with valid ticket should delete ticket and return response")
    public void sellBackLottery_ValidTicket_ReturnsResponseAndDeletesTicket(){

        // Arrange
        String ticketId = "000001";
        Lottery lottery = new Lottery(ticketId,80,1);
        String userId = "0123456789";
        User user = new User(userId);
        UserTicket userTicket = new UserTicket(lottery,user);
        userTicket.setId(1);

        when(userTicketRepository.findByUserIdAndTicketId(userId,ticketId)).thenReturn((List.of(userTicket)));
        doNothing().when(userTicketRepository).delete(any(UserTicket.class));

        // Act
        LotterySellBackResponse actual = lotteryService.sellBackLottery(userId,ticketId);

        // Assert
        assertEquals(ticketId,actual.ticketId());
        verify(userTicketRepository,times(1)).delete(userTicket);
    }

    @Test
    @DisplayName("Sell back lottery with non-existing ticket should throw NotFoundException")
    void sellBackLottery_NonExistingTicket_ThrowsNotFoundException(){

        // Arrange
        String userId = "0123456789";
        String ticketId = "123456";

        when(userTicketRepository.findByUserIdAndTicketId(userId,ticketId)).thenReturn(List.of());

        // Act & Assert
        assertThrows(NotFoundException.class,() -> lotteryService.sellBackLottery(userId,ticketId));
    }

    @Test
    @DisplayName("Sell back lottery with database error should throw InternalServerException")
    void sellBackLottery_DatabaseError_ThrowsInternalServerException(){

        String ticketId = "000001";
        Lottery lottery = new Lottery(ticketId,80,1);
        String userId = "0123456789";
        User user = new User(userId);
        UserTicket userTicket = new UserTicket(lottery,user);
        userTicket.setId(1);

        when(userTicketRepository.findByUserIdAndTicketId(userId,ticketId)).thenReturn(List.of(userTicket));
        doThrow(new RuntimeException("Database connection error")).when(userTicketRepository).delete(any(UserTicket.class));

        // Act & Assert
        assertThrows(InternalServerException.class,() -> lotteryService.sellBackLottery(userId,ticketId));
    }
    //endregion
}