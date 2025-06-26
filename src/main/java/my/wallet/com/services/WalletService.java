package my.wallet.com.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import my.wallet.com.models.User;
import my.wallet.com.models.Wallet;
import my.wallet.com.models.WalletHistory;
import my.wallet.com.repositories.WalletHistoricalRepository;
import my.wallet.com.repositories.WalletRepository;
import my.wallet.com.vos.WalletBalance;
import my.wallet.com.vos.WalletRequest;
import my.wallet.com.vos.WalletTransferRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
  private final WalletRepository repository;
  private final WalletHistoricalRepository walletHistoricalRepository;
  private final UserService userService;

  @Lazy
  public WalletService(
      final WalletRepository repository,
      final UserService userService,
      final WalletHistoricalRepository walletHistoricalRepository) {
    this.repository = repository;
    this.userService = userService;
    this.walletHistoricalRepository = walletHistoricalRepository;
  }

  @Transactional
  @Retryable(
      retryFor = {ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100))
  public void transferAmount(final WalletTransferRequest walletTransferRequest) {
    validateUserTransferForTheSameUser(walletTransferRequest);
    final BigDecimal amount = walletTransferRequest.amount();
    final User userFrom = userService.findUserByCpf(walletTransferRequest.from());
    final Wallet userFromWallet = userFrom.getWallet();
    if (balanceAvailable(userFromWallet, amount)) {
      final User userTo = userService.findUserByCpf(walletTransferRequest.to());
      Wallet userToWallet = userTo.getWallet();
      withdrawAmount(userFrom.getWallet(), amount);
      depositAmount(userToWallet, amount);
      repository.save(userFromWallet);
      repository.save(userToWallet);

      saveWalletHistory(userFromWallet);
      saveWalletHistory(userTo.getWallet());
    }
  }

  @Transactional
  @Retryable(
      retryFor = {ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
      maxAttempts = 2,
      backoff = @Backoff(delay = 100))
  public void depositAmount(final WalletRequest walletRequest) {
    final User user = userService.findUserByCpf(walletRequest.cpf());

    final Wallet wallet = user.getWallet();
    depositAmount(wallet, walletRequest.amount());
    repository.save(wallet);

    saveWalletHistory(wallet);
  }

  @Transactional
  @Retryable(
      retryFor = {ObjectOptimisticLockingFailureException.class, OptimisticLockException.class},
      maxAttempts = 2,
      backoff = @Backoff(delay = 100))
  public void withdrawAmount(final WalletRequest walletRequest) {
    final User user = userService.findUserByCpf(walletRequest.cpf());
    final Wallet wallet = user.getWallet();
    withdrawAmount(wallet, walletRequest.amount());
    repository.save(wallet);

    saveWalletHistory(wallet);
  }

  public void createWallet(User user) {
    final Wallet wallet = new Wallet(user, BigDecimal.ZERO);
    repository.save(wallet);
  }

  public WalletBalance getUserBalance(final String cpf) {
    final User user = userService.findUserByCpf(cpf);
    return new WalletBalance(user.getWallet().getAmount());
  }

  public WalletBalance getUserHistoricalBalance(final String cpf, final LocalDateTime date) {
    final User user = userService.findUserByCpf(cpf);
    final WalletHistory walletHistory =
        walletHistoricalRepository
            .findTopByWalletIdAndTransactionTimeLessThanEqualOrderByTransactionTimeDesc(
                user.getWallet().getId(), date)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format("Not found any amount in the date %s or before", date)));
    return new WalletBalance(walletHistory.getAmount());
  }

  private void saveWalletHistory(final Wallet wallet) {
    final WalletHistory walletHistory =
        new WalletHistory(wallet.getId(), wallet.getAmount(), LocalDateTime.now());
    walletHistoricalRepository.save(walletHistory);
  }

  private void validateUserTransferForTheSameUser(
      final WalletTransferRequest walletTransferRequest) {
    if (walletTransferRequest.from().equals(walletTransferRequest.to())) {
      throw new IllegalArgumentException("User cannot transfer to himself");
    }
  }

  private void depositAmount(Wallet wallet, BigDecimal deposit) {
    if (deposit == null || deposit.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Deposit amount must be positive and non-null");
    wallet.setAmount(wallet.getAmount().add(deposit));
  }

  private void withdrawAmount(Wallet wallet, BigDecimal withdraw) {
    if (withdraw == null || withdraw.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Withdraw amount must be positive and non-null");
    if (withdraw.compareTo(wallet.getAmount()) > 0)
      throw new IllegalArgumentException(
          "Insufficient funds: cannot withdraw more than the current balance");
    wallet.setAmount(wallet.getAmount().subtract(withdraw));
  }

  private boolean balanceAvailable(Wallet wallet, BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Transfer amount must be positive and non-null");
    if (amount.compareTo(wallet.getAmount()) > 0)
      throw new IllegalArgumentException(
          "Insufficient funds: cannot transfer more than the current balance");
    return true;
  }
}
