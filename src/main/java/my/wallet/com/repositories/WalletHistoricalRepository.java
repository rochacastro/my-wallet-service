package my.wallet.com.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import my.wallet.com.models.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoricalRepository extends JpaRepository<WalletHistory, UUID> {

  Optional<WalletHistory>
      findTopByWalletIdAndTransactionTimeLessThanEqualOrderByTransactionTimeDesc(
          UUID walletId, LocalDateTime before);
}
