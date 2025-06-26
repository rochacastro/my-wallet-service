package my.wallet.com.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import my.wallet.com.controllers.WalletController;
import my.wallet.com.services.WalletService;
import my.wallet.com.vos.WalletBalance;
import my.wallet.com.vos.WalletRequest;
import my.wallet.com.vos.WalletTransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(WalletController.class)
class WalletControllerTest {

  private static final String PATH = "/api/v1/wallet";
  private static final String VALID_CPF = "27175250096";
  private static final String VALID_CPF_TO_TRANSFER = "26936761003";

  @Autowired private MockMvc mockMvc;

  @MockBean private WalletService walletService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testDepositByCpf() throws Exception {
    WalletRequest request = new WalletRequest(VALID_CPF, BigDecimal.valueOf(100.0));

    mockMvc
        .perform(
            patch(PATH + "/v1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("requestTraceId", "123"))
        .andExpect(status().isOk());

    verify(walletService).depositAmount(any(WalletRequest.class));
  }

  @Test
  void testWithdrawByCpf() throws Exception {
    WalletRequest request = new WalletRequest(VALID_CPF, BigDecimal.valueOf(50.0));

    mockMvc
        .perform(
            patch(PATH + "/v1/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("requestTraceId", "123"))
        .andExpect(status().isOk());

    verify(walletService).withdrawAmount(any(WalletRequest.class));
  }

  @Test
  void testBalance() throws Exception {
    WalletBalance balance = new WalletBalance(BigDecimal.valueOf(150.0));

    when(walletService.getUserBalance(VALID_CPF)).thenReturn(balance);

    mockMvc
        .perform(get(PATH + "/v1/balance").param("cpf", VALID_CPF).header("requestTraceId", "123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(150.0));
  }

  @Test
  void testHistoricalBalance() throws Exception {
    LocalDateTime date = LocalDateTime.now().minusDays(1);
    WalletBalance balance = new WalletBalance(BigDecimal.valueOf(120.0));

    when(walletService.getUserHistoricalBalance(eq(VALID_CPF), any(LocalDateTime.class)))
        .thenReturn(balance);

    mockMvc
        .perform(
            get(PATH + "/v1/historical-balance")
                .param("cpf", VALID_CPF)
                .param("date", date.toString())
                .header("requestTraceId", "123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance").value(120.0));
  }

  @Test
  void testTransferAmount() throws Exception {
    WalletTransferRequest request =
        new WalletTransferRequest(VALID_CPF, VALID_CPF_TO_TRANSFER, BigDecimal.valueOf(30.0));

    mockMvc
        .perform(
            patch(PATH + "/v1/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("requestTraceId", "123"))
        .andExpect(status().isOk());

    verify(walletService).transferAmount(any(WalletTransferRequest.class));
  }
}
