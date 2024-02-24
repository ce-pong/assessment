package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryController.class)
class LotteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Test
    @DisplayName("Call get available ticket should return status code 200 and correct lottery list response")
    public void getAvailableTickets_ShouldReturn200AndLotteryListResponse() throws Exception {
        // Arrange
        when(lotteryService.getAvailableTicketIds()).thenReturn(new LotteryListResponse(List.of("000001", "000002", "123456")));

        // Act & Assert
        mockMvc.perform(get("/lotteries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tickets").isArray())
                .andExpect(jsonPath("$.tickets[0]").value("000001"))
                .andExpect(jsonPath("$.tickets[1]").value("000002"))
                .andExpect(jsonPath("$.tickets[2]").value("123456"));
    }

    @Test
    @DisplayName("Handling Internal Server Error When Retrieving Available Tickets should return status code 500 and defined message")
    public void whenInternalServerOccur_ShouldReturn500AndDefinedMessage() throws Exception {

        // Arrange
        when(lotteryService.getAvailableTicketIds()).thenThrow(new InternalServerException("Failed to get available ticket"));

        // Act & Assert
        mockMvc.perform(get("/lotteries"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to get available ticket"));
    }



}
