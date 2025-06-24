package my.wallet.com.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import my.wallet.com.models.User;
import my.wallet.com.models.Wallet;
import my.wallet.com.models.WalletHistory;
import my.wallet.com.repositories.WalletHistoricalRepository;
import my.wallet.com.repositories.WalletRepository;
import my.wallet.com.vos.WalletTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletProcessorService {

  private final WalletRepository repository;
  private final WalletHistoricalRepository walletHistoricalRepository;
  private final UserService userService;

  public WalletProcessorService(
      final WalletRepository repository,
      final WalletHistoricalRepository walletHistoricalRepository,
      final UserService userService) {
    this.repository = repository;
    this.walletHistoricalRepository = walletHistoricalRepository;
    this.userService = userService;
  }

  @Transactional
  public void transferAmount(final WalletTransferRequest walletTransferRequest) {
    validateUserTransferForTheSameUser(walletTransferRequest);
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

  public void saveWalletHistory(final Wallet wallet) {
    final WalletHistory walletHistory =
        new WalletHistory(wallet.getId(), wallet.getAmount(), LocalDateTime.now());
    walletHistoricalRepository.save(walletHistory);
  }

  private void validateUserTransferForTheSameUser(
      final WalletTransferRequest walletTransferRequest) {
    if (walletTransferRequest.from().equals(walletTransferRequest.to())) {
      throw new IllegalArgumentException("User cannot transfer to itself");
    }
  }
}
