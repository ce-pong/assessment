package com.kbtg.bootcamp.posttest.lottery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import com.kbtg.bootcamp.posttest.securityconfig.SecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LotteryController.class)
@Import(SecurityConfiguration.class)
class LotteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("getAvailableTickets Should Return Status Code 200 and Correct Lottery List Response")
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
    @DisplayName("getAvailableTickets returns status 500 and error message on internal server error")
    public void getAvailableTickets_OnInternalServerError_ShouldReturnStatus500AndErrorMessage() throws Exception {

        // Arrange
        when(lotteryService.getAvailableTicketIds()).thenThrow(new InternalServerException("Failed to get available ticket"));

        // Act & Assert
        mockMvc.perform(get("/lotteries"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to get available ticket"));
    }

    @Test
    @DisplayName("CreateLottery Attached Basic Auth (Admin) should return status code 201 and correct response")
    public void createTicket_ShouldReturn201AndResponseContainTicketId() throws Exception {

        // Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        LotteryResponse expectedResponse = new LotteryResponse(request.ticket());
        when(lotteryService.createLottery(request)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/admin/lotteries")
                        .with(httpBasic("admin","password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticket").value("000001"));

    }

    @Test
    @DisplayName("CreateLottery with invalid data should return status code 400")
    public void createTicket_withInvalidData_ShouldReturn400() throws Exception {

        // Arrange
        LotteryDto request = new LotteryDto("000001",0,0);

        // Act & Assert
        mockMvc.perform(post("/admin/lotteries")
                        .with(httpBasic("admin","password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Validation error"));
    }



}
