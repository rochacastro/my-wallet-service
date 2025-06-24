package my.wallet.com.services;

import jakarta.persistence.EntityNotFoundException;
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
  public void transferAmount(final WalletTransferRequest walletTransferRequest) {
    final BigDecimal amount = walletTransferRequest.amount();
    final User userFrom = userService.findUserByCpf(walletTransferRequest.from());
    final Wallet userFromWallet = userFrom.getWallet();
    if (userFromWallet.balanceAvailable(amount)) {
      final User userTo = userService.findUserByCpf(walletTransferRequest.to());
      userFromWallet.withdrawAmount(amount);
      userTo.getWallet().depositAmount(amount);
      repository.save(userFromWallet);
      repository.save(userTo.getWallet());

      saveWalletHistory(userFromWallet);
      saveWalletHistory(userTo.getWallet());
    }
  }

  @Transactional
  public void depositAmount(final WalletRequest walletRequest) {
    final User user = userService.findUserByCpf(walletRequest.cpf());

    final Wallet wallet = user.getWallet();
    wallet.depositAmount(walletRequest.amount());
    repository.save(wallet);

    saveWalletHistory(wallet);
  }

  @Transactional
  public void withdrawAmount(final WalletRequest walletRequest) {
    final User user = userService.findUserByCpf(walletRequest.cpf());
    final Wallet wallet = user.getWallet();
    wallet.withdrawAmount(walletRequest.amount());
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
                        String.format("Not founded any amount in the date %s or before", date)));
    return new WalletBalance(walletHistory.getAmount());
  }

  private void saveWalletHistory(final Wallet wallet) {
    final WalletHistory walletHistory =
        new WalletHistory(wallet.getId(), wallet.getAmount(), LocalDateTime.now());
    walletHistoricalRepository.save(walletHistory);
  }
}
