package com.kbtg.bootcamp.posttest.lottery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbtg.bootcamp.posttest.exception.InternalServerException;
import com.kbtg.bootcamp.posttest.exception.LotteryRunOutException;
import com.kbtg.bootcamp.posttest.exception.NotFoundException;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    //region EXP01
    @Test
    @DisplayName("CreateLottery with Basic Auth (Admin) should return status code 201 and correct response containing Ticket ID")
    public void createLottery_WithAdminAuth_Returns201AndResponseWithTicketId()throws Exception {

        //Arrange
        LotteryDto request = new LotteryDto("000001",80,1);
        LotteryResponse expectedResponse = new LotteryResponse(request.ticket());
        when(lotteryService.addLottery(request)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(post("/admin/lotteries")
                        .with(httpBasic("admin","password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticket").value("000001"));

    }

    @Test
    @DisplayName("CreateLottery with invalid data should return status code 400 from MethodArgumentNotValidException")
    public void createLottery_WithInvalidData_Returns400FromMethodArgumentNotValidException()throws Exception {

        //Arrange
        LotteryDto request = new LotteryDto("000001",0,0);

        //Act & Assert
        mockMvc.perform(post("/admin/lotteries")
                        .with(httpBasic("admin","password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    //endregion

    //region EXP02
    @Test
    @DisplayName("GetAvailableTickets should return status code 200 and correct lottery list response")
    public void getAvailableTickets_Returns200AndCorrectLotteryListResponse()  throws Exception {
        //Arrange
        when(lotteryService.getAvailableTicketIds()).thenReturn(new LotteryListResponse(List.of("000001", "000002", "123456")));

        //Act & Assert
        mockMvc.perform(get("/lotteries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tickets").isArray())
                .andExpect(jsonPath("$.tickets[0]").value("000001"))
                .andExpect(jsonPath("$.tickets[1]").value("000002"))
                .andExpect(jsonPath("$.tickets[2]").value("123456"));
    }

    @Test
    @DisplayName("GetAvailableTickets returns status 500 and error message on internal server error from InternalServerException")
    public void getAvailableTickets_Failed_ReturnsStatus500AndErrorMessageFromInternalServerException() throws Exception {

        //Arrange
        when(lotteryService.getAvailableTicketIds()).thenThrow(new InternalServerException("Failed to get available ticket"));

        //Act & Assert
        mockMvc.perform(get("/lotteries"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to get available ticket"));
    }
    //endregion

    //region EXP03
    @Test
    @DisplayName("PurchaseLottery should return status code 201 and correct response")
    public void purchaseLottery_Return201AndCorrectResponse() throws Exception {

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        LotteryPurchaseResponse expectedResponse = new LotteryPurchaseResponse("1");
        when(lotteryService.purchaseLottery(userId,ticketId)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(post("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedResponse.id()));
    }

    @Test
    @DisplayName("PurchaseLottery with no existing ticket return status code 404 from NotFoundException")
    public void purchaseLottery_withNoExistingTicket_Return404FromNotFoundException() throws Exception {

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        String expectedMessage = "Ticket " + ticketId + " not found";
        doThrow(new NotFoundException(expectedMessage))
                .when(lotteryService).purchaseLottery(userId,ticketId);



        //Act & Assert
        mockMvc.perform(post("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("PurchaseLottery with run out ticket return status code 409 from LotteryRunOutException")
    public void purchaseLottery_withRunOutTicket_Return409FromLotteryRunOutException() throws Exception {

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        String expectedMessage = "Ticket "+ticketId+" has already been purchased";
        doThrow(new LotteryRunOutException(expectedMessage))
                .when(lotteryService).purchaseLottery(userId,ticketId);


        //Act & Assert
        mockMvc.perform(post("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("PurchaseLottery returns status 500 and error message on internal server error from InternalServerException")
    public void purchaseLottery_Failed_Return500FromLotteryInternalServerException() throws Exception {

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        String expectedMessage = "Failed to purchase lottery";
        doThrow(new InternalServerException(expectedMessage))
                .when(lotteryService).purchaseLottery(userId,ticketId);


        //Act & Assert
        mockMvc.perform(post("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
    //endregion

    //region EXP04
    @Test
    @DisplayName("GetPurchaseHistory should return status code 200 and correct response")
    public void getPurchaseHistory_Return200AndCorrectResponse() throws Exception{

        //Arrange
        String userId = "0123456789";
        LotteryHistoryResponse expectedResponse = new LotteryHistoryResponse(
                List.of("000001","000002"),
                2,
                200
        );
        when(lotteryService.getPurchaseHistory(userId)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(get("/users/"+userId+"/lotteries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tickets").isArray())
                .andExpect(jsonPath("$.tickets[0]").value("000001"))
                .andExpect(jsonPath("$.tickets[1]").value("000002"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.cost").value(200));

    }
    @Test
    @DisplayName("GetPurchaseHistory with invalid user should return status code 400 from ConstraintViolationException")
    public void getPurchaseHistory_withInvalidUser_Return400FromConstraintViolationException() throws Exception {

        //Arrange
        String userId = "123";

        //Act & Assert
        mockMvc.perform(get("/users/"+userId+"/lotteries"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
    //endregion

    //region EXP05
    @Test
    @DisplayName("SellBackLottery should return status code 200 with correct response")
    public void sellBackLottery_Returns200AndCorrectLotterySellBackResponse() throws Exception{

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        LotterySellBackResponse expectedResponse = new LotterySellBackResponse(ticketId);
        when(lotteryService.sellBackLottery(userId,ticketId)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(delete("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticket").value(expectedResponse.ticketId()));
    }
    @Test
    @DisplayName("SellBackLottery with no existing ticket return status code 404 from NotFoundException")
    public void sellBackLottery_withNoExistingTicket_Returns404FromNotFoundException() throws Exception{

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        String expectedMessage = "Not found ticket "+ticketId+" for user "+userId;
        doThrow(new NotFoundException(expectedMessage))
                .when(lotteryService).sellBackLottery(userId,ticketId);

        //Act & Assert
        mockMvc.perform(delete("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("SellBackLottery returns status 500 and error message on internal server error from InternalServerException")
    public void sellBackLottery_Failed_Return500FromLotteryInternalServerException() throws Exception{

        //Arrange
        String userId = "0123456789";
        String ticketId = "000001";
        String expectedMessage = "Failed to sell lottery back";
        doThrow(new InternalServerException(expectedMessage))
                .when(lotteryService).sellBackLottery(userId,ticketId);

        //Act & Assert
        mockMvc.perform(delete("/users/"+userId+"/lotteries/"+ticketId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
    //endregion
}
