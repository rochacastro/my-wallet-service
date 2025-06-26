package com.my.wallet.repositories;

import com.my.wallet.models.WalletHistory;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoricalRepository extends JpaRepository<WalletHistory, UUID> {

  Optional<WalletHistory>
      findTopByWalletIdAndTransactionTimeLessThanEqualOrderByTransactionTimeDesc(
          UUID walletId, LocalDateTime before);
}
