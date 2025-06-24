package my.wallet.com.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import my.wallet.com.vos.WalletBalance;
import my.wallet.com.vos.WalletRequest;
import my.wallet.com.vos.WalletTransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@ExtendWith(MockitoExtension.class)
class WalletHandlerServiceTest {
  @Mock private WalletService walletService;

  @InjectMocks private WalletHandlerService walletHandlerService;

  @Test
  void shouldCallWalletServiceOnceOnSuccess() {
    WalletTransferRequest request = mock(WalletTransferRequest.class);
    doNothing().when(walletService).transferAmount(request);
    walletHandlerService.transferAmountWithRetry(request);
    verify(walletService, times(1)).transferAmount(request);
  }

  @Test
  void shouldRetryOnOptimisticLockExceptionAndSucceed() {
    WalletTransferRequest request = mock(WalletTransferRequest.class);
    doThrow(new ObjectOptimisticLockingFailureException("Wallet", "id"))
        .doNothing()
        .when(walletService)
        .transferAmount(request);
    walletHandlerService.transferAmountWithRetry(request);
    verify(walletService, times(2)).transferAmount(request);
  }

  @Test
  void shouldFailAfterMaxRetries() {
    WalletTransferRequest request = mock(WalletTransferRequest.class);
    doThrow(new OptimisticLockException("fail")).when(walletService).transferAmount(request);
    assertThrows(
        OptimisticLockException.class, () -> walletHandlerService.transferAmountWithRetry(request));
    verify(walletService, times(4)).transferAmount(request);
  }

  @Test
  void shouldDelegateDepositAmount() {
    WalletRequest request = mock(WalletRequest.class);
    doNothing().when(walletService).depositAmount(request);
    walletHandlerService.depositAmount(request);
    verify(walletService, times(1)).depositAmount(request);
  }

  @Test
  void shouldDelegateWithdrawAmount() {
    WalletRequest request = mock(WalletRequest.class);
    doNothing().when(walletService).withdrawAmount(request);
    walletHandlerService.withdrawAmount(request);
    verify(walletService, times(1)).withdrawAmount(request);
  }

  @Test
  void shouldDelegateGetUserBalance() {
    String cpf = "12345678900";
    WalletBalance expected = mock(WalletBalance.class);
    when(walletService.getUserBalance(cpf)).thenReturn(expected);
    WalletBalance result = walletHandlerService.getUserBalance(cpf);
    assertSame(expected, result);
    verify(walletService, times(1)).getUserBalance(cpf);
  }

  @Test
  void shouldDelegateGetUserHistoricalBalance() {
    String cpf = "12345678900";
    LocalDateTime date = LocalDateTime.now();
    WalletBalance expected = mock(WalletBalance.class);
    when(walletService.getUserHistoricalBalance(cpf, date)).thenReturn(expected);
    WalletBalance result = walletHandlerService.getUserHistoricalBalance(cpf, date);
    assertSame(expected, result);
    verify(walletService, times(1)).getUserHistoricalBalance(cpf, date);
  }
}
