package com.my.wallet.repositories;

import com.my.wallet.models.Wallet;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
  Optional<Wallet> findByUserId(UUID id);
}
