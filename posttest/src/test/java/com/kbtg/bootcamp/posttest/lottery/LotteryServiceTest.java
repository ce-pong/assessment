package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LotteryServiceTest {

    private LotteryRepository lotteryRepository;

    private LotteryService lotteryService;

    @BeforeEach
    void setUp() {
        lotteryRepository = Mockito.mock(LotteryRepository.class);
        lotteryService = new LotteryService(lotteryRepository);
    }

    @Test
    @DisplayName("Get Available Ticket IDs With No Data Should Return Empty List Response")
    void getAvailableTicketIds_withNoData_ShouldReturnResponseWithEmptyList() {
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
    void getAvailableTicketIds_withNoData_ShouldReturnResponseWithListStringTicketId() {
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
    void shouldThrowInternalServerException_WhenRetrievingTicketsFails() {
        // Arrange
        when(lotteryRepository.findByAmountMoreThanZero()).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(InternalServerException.class, () -> {lotteryService.getAvailableTicketIds();});
    }
}