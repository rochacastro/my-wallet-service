package my.wallet.com.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import my.wallet.com.models.User;
import my.wallet.com.models.Wallet;
import my.wallet.com.models.WalletHistory;
import my.wallet.com.repositories.WalletHistoricalRepository;
import my.wallet.com.repositories.WalletRepository;
import my.wallet.com.util.UserBuild;
import my.wallet.com.vos.WalletBalance;
import my.wallet.com.vos.WalletRequest;
import my.wallet.com.vos.WalletTransferRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
class WalletServiceTest {
  private static final String VALID_CPF = "26936761003";
  private static final String VALID_CPF_2 = "27175250096";

  @Mock private WalletRepository walletRepository;

  @Mock private WalletHistoricalRepository walletHistoricalRepository;

  @Mock private UserService userService;

  @InjectMocks private WalletService walletService;

  @SpyBean private WalletService spyWalletService;

  @Test
  void shouldTransferAmountSuccessfully() {
    User fromUser =
        UserBuild.build(UUID.randomUUID(), "Antigo Nome", VALID_CPF, BigDecimal.valueOf(500));
    User toUser = UserBuild.build(UUID.randomUUID(), "João", VALID_CPF, BigDecimal.ZERO);

    BigDecimal amount = BigDecimal.valueOf(50);
    WalletTransferRequest transferRequest =
        new WalletTransferRequest(VALID_CPF, VALID_CPF_2, amount);

    when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);
    when(userService.findUserByCpf(VALID_CPF_2)).thenReturn(toUser);

    walletService.transferAmount(transferRequest);

    verify(walletRepository, times(2)).save(any(Wallet.class));
    verify(walletHistoricalRepository, times(2)).save(any(WalletHistory.class));
  }

  @Test
  void shouldDepositAmountAndSaveHistory() {
    BigDecimal amount = BigDecimal.valueOf(100);
    WalletRequest request = new WalletRequest(VALID_CPF, amount);
    Wallet wallet = new Wallet();
    wallet.setId(UUID.randomUUID());
    wallet.setAmount(BigDecimal.ZERO);

    User user = mock(User.class);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(user);
    when(user.getWallet()).thenReturn(wallet);

    walletService.depositAmount(request);

    assertEquals(amount, wallet.getAmount());
    verify(walletRepository).save(wallet);
    verify(walletHistoricalRepository).save(any(WalletHistory.class));
  }

  @Test
  void shouldWithdrawAmountAndSaveHistory() {
    BigDecimal amount = BigDecimal.valueOf(50);
    WalletRequest request = new WalletRequest(VALID_CPF, amount);

    Wallet wallet = new Wallet();
    wallet.setId(UUID.randomUUID());
    wallet.setAmount(BigDecimal.valueOf(100));

    User user = mock(User.class);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(user);
    when(user.getWallet()).thenReturn(wallet);

    walletService.withdrawAmount(request);

    assertEquals(BigDecimal.valueOf(50), wallet.getAmount());
    verify(walletRepository).save(wallet);
    verify(walletHistoricalRepository).save(any(WalletHistory.class));
  }

  @Test
  void shouldCreateWalletWithZeroBalance() {
    User user = mock(User.class);
    ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

    walletService.createWallet(user);

    verify(walletRepository).save(captor.capture());
    Wallet createdWallet = captor.getValue();
    assertEquals(BigDecimal.ZERO, createdWallet.getAmount());
    assertEquals(user, createdWallet.getUser());
  }

  @Test
  void shouldReturnUserBalance() {
    Wallet wallet = new Wallet();
    wallet.setAmount(BigDecimal.valueOf(200));

    User user = mock(User.class);
    when(user.getWallet()).thenReturn(wallet);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(user);

    WalletBalance balance = walletService.getUserBalance(VALID_CPF);

    assertEquals(BigDecimal.valueOf(200), balance.balance());
  }

  @Test
  void shouldReturnHistoricalBalance() {
    LocalDateTime date = LocalDateTime.now().minusDays(1);

    Wallet wallet = new Wallet();
    wallet.setId(UUID.randomUUID());

    WalletHistory history = new WalletHistory(UUID.randomUUID(), BigDecimal.valueOf(300), date);
    User user = mock(User.class);

    when(user.getWallet()).thenReturn(wallet);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(user);
    when(walletHistoricalRepository
            .findTopByWalletIdAndTransactionTimeLessThanEqualOrderByTransactionTimeDesc(
                wallet.getId(), date))
        .thenReturn(Optional.of(history));

    WalletBalance balance = walletService.getUserHistoricalBalance(VALID_CPF, date);

    assertEquals(BigDecimal.valueOf(300), balance.balance());
  }

  @Test
  void shouldThrowIfNoHistoricalBalanceFound() {
    LocalDateTime date = LocalDateTime.now().minusDays(1);

    Wallet wallet = new Wallet();
    wallet.setId(UUID.randomUUID());
    User user = mock(User.class);

    when(user.getWallet()).thenReturn(wallet);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(user);
    when(walletHistoricalRepository
            .findTopByWalletIdAndTransactionTimeLessThanEqualOrderByTransactionTimeDesc(
                wallet.getId(), date))
        .thenReturn(Optional.empty());

    EntityNotFoundException exception =
        assertThrows(
            EntityNotFoundException.class,
            () -> walletService.getUserHistoricalBalance(VALID_CPF, date));

    assertTrue(exception.getMessage().contains("Not found any amount in the date"));
  }

  @Test
  void shouldThrowWhenInsufficientFundsOnTransfer() {
    User fromUser =
        UserBuild.build(UUID.randomUUID(), "Antigo Nome", VALID_CPF, BigDecimal.valueOf(50));
    BigDecimal amount = BigDecimal.valueOf(1000);
    WalletTransferRequest transferRequest =
        new WalletTransferRequest(VALID_CPF, VALID_CPF_2, amount);

    when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> walletService.transferAmount(transferRequest));
    assertTrue(exception.getMessage().contains("Insufficient funds"));
  }

  @Test
  void shouldThrowWhenTransferAmountIsNull() {
    WalletTransferRequest transferRequest = new WalletTransferRequest(VALID_CPF, VALID_CPF_2, null);
    User fromUser = mock(User.class);
    when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);
    Wallet fromWallet = mock(Wallet.class);
    when(fromUser.getWallet()).thenReturn(fromWallet);
    /*when(fromWallet.balanceAvailable(null))
    .thenThrow(new IllegalArgumentException("Transfer amount must be positive and non-null"));*/

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> walletService.transferAmount(transferRequest));
    assertTrue(exception.getMessage().contains("positive and non-null"));
  }

  @Test
  void shouldThrowWhenTransferAmountIsZeroOrNegative() {
    BigDecimal[] invalidAmounts = {BigDecimal.ZERO, BigDecimal.valueOf(-10)};
    for (BigDecimal invalidAmount : invalidAmounts) {
      WalletTransferRequest transferRequest =
          new WalletTransferRequest(VALID_CPF, VALID_CPF_2, invalidAmount);
      User fromUser = mock(User.class);
      when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);
      Wallet fromWallet = mock(Wallet.class);
      when(fromUser.getWallet()).thenReturn(fromWallet);
      /*when(fromWallet.balanceAvailable(invalidAmount))
      .thenThrow(new IllegalArgumentException("Transfer amount must be positive and non-null"));*/

      IllegalArgumentException exception =
          assertThrows(
              IllegalArgumentException.class, () -> walletService.transferAmount(transferRequest));
      assertTrue(exception.getMessage().contains("positive and non-null"));
    }
  }

  @Test
  void shouldSaveBothWalletsAndHistoriesOnTransfer() {
    User fromUser =
        UserBuild.build(UUID.randomUUID(), "Antigo Nome", VALID_CPF, BigDecimal.valueOf(500));
    User toUser = UserBuild.build(UUID.randomUUID(), "João", VALID_CPF, BigDecimal.ZERO);
    BigDecimal amount = BigDecimal.valueOf(25);
    WalletTransferRequest transferRequest =
        new WalletTransferRequest(VALID_CPF, VALID_CPF_2, amount);

    when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);
    when(userService.findUserByCpf(VALID_CPF_2)).thenReturn(toUser);

    walletService.transferAmount(transferRequest);

    verify(walletRepository).save(fromUser.getWallet());
    verify(walletRepository).save(toUser.getWallet());
    verify(walletHistoricalRepository, times(2)).save(any(WalletHistory.class));
  }

  @Test
  void shouldThrowWhenTransferAmountToTheSameUser() {
    WalletTransferRequest transferRequest =
        new WalletTransferRequest(VALID_CPF, VALID_CPF, BigDecimal.valueOf(100));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> walletService.transferAmount(transferRequest));
    assertTrue(exception.getMessage().contains("User cannot transfer to himself"));
  }

  @Test
  void shouldThrowOptimisticLockExceptionOnRepositorySave() {
    User fromUser =
        UserBuild.build(UUID.randomUUID(), "Antigo Nome", VALID_CPF, BigDecimal.valueOf(500));
    User toUser = UserBuild.build(UUID.randomUUID(), "João", VALID_CPF, BigDecimal.ZERO);
    BigDecimal amount = BigDecimal.valueOf(50);
    WalletTransferRequest transferRequest =
        new WalletTransferRequest(VALID_CPF, VALID_CPF_2, amount);

    when(userService.findUserByCpf(VALID_CPF)).thenReturn(fromUser);
    when(userService.findUserByCpf(VALID_CPF_2)).thenReturn(toUser);

    doThrow(new OptimisticLockException("Optimistic lock error"))
        .when(walletRepository)
        .save(any(Wallet.class));

    assertThrows(
        OptimisticLockException.class, () -> walletService.transferAmount(transferRequest));
    verify(walletRepository).save(fromUser.getWallet());
  }

  @Test
  void shouldRetryOnOptimisticLockExceptionAndSucceed() {
    WalletTransferRequest request = mock(WalletTransferRequest.class);

    doThrow(new ObjectOptimisticLockingFailureException("Wallet", "id"))
        .doThrow(new ObjectOptimisticLockingFailureException("Wallet", "id"))
        .doNothing()
        .when(spyWalletService)
        .transferAmount(request);

    spyWalletService.transferAmount(request);

    verify(spyWalletService, times(3)).transferAmount(request);
  }

  @Test
  void shouldFailAfterMaxAttempts() {
    WalletTransferRequest request = mock(WalletTransferRequest.class);

    doThrow(new ObjectOptimisticLockingFailureException("Wallet", "id"))
        .when(spyWalletService)
        .transferAmount(request);

    assertThrows(
        ObjectOptimisticLockingFailureException.class,
        () -> spyWalletService.transferAmount(request));
    verify(spyWalletService, times(3)).transferAmount(request);
  }
}
