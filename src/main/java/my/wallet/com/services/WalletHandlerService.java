package my.wallet.com.services;

import jakarta.persistence.OptimisticLockException;
import java.time.LocalDateTime;
import my.wallet.com.vos.WalletBalance;
import my.wallet.com.vos.WalletRequest;
import my.wallet.com.vos.WalletTransferRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class WalletHandlerService {
  private final WalletService walletService;

  public WalletHandlerService(final WalletService walletService) {
    this.walletService = walletService;
  }

  public void transferAmountWithRetry(WalletTransferRequest walletTransferRequest) {
    runWithOptimisticLockRetry(() -> walletService.transferAmount(walletTransferRequest));
  }

  public void depositAmount(final WalletRequest walletRequest) {
    runWithOptimisticLockRetry(() -> walletService.depositAmount(walletRequest));
  }

  public void withdrawAmount(final WalletRequest walletRequest) {
    runWithOptimisticLockRetry(() -> walletService.withdrawAmount(walletRequest));
  }

  public WalletBalance getUserBalance(final String cpf) {
    return walletService.getUserBalance(cpf);
  }

  public WalletBalance getUserHistoricalBalance(final String cpf, final LocalDateTime date) {
    return walletService.getUserHistoricalBalance(cpf, date);
  }

  private void runWithOptimisticLockRetry(Runnable operation) {
    int maxRetries = 3;
    int attempt = 0;
    while (true) {
      try {
        operation.run();
        break;
      } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
        if (++attempt > maxRetries) {
          throw e;
        }
        try {
          Thread.sleep(1000); // Optional: backoff
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ie);
        }
      }
    }
  }
}
