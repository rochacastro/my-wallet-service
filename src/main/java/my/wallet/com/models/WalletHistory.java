package my.wallet.com.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class WalletHistory {
  @Id @GeneratedValue private UUID id;
  private UUID walletId;

  @Column(precision = 19, scale = 4, nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDateTime transactionTime;

  public WalletHistory() {}

  public WalletHistory(UUID walletId, BigDecimal amount, LocalDateTime transactionTime) {
    this.walletId = walletId;
    this.amount = amount;
    this.transactionTime = transactionTime;
  }

  public UUID getWalletId() {
    return walletId;
  }

  public void setWalletId(UUID walletId) {
    this.walletId = walletId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getTransactionTime() {
    return transactionTime;
  }

  public void setTransactionTime(LocalDateTime transactionTime) {
    this.transactionTime = transactionTime;
  }
}
