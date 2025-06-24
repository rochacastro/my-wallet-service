package my.wallet.com.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Wallet {

  @Id @GeneratedValue private UUID id;

  @NotNull
  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  private BigDecimal amount;

  public Wallet(User user, BigDecimal amount) {
    this.user = user;
    this.amount = amount != null ? amount : BigDecimal.ZERO;
  }

  public Wallet() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void depositAmount(BigDecimal deposit) {
    if (deposit == null || deposit.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Deposit amount must be positive and non-null");
    this.amount = this.amount.add(deposit);
  }

  public void withdrawAmount(BigDecimal withdraw) {
    if (withdraw == null || withdraw.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Withdraw amount must be positive and non-null");
    if (withdraw.compareTo(this.amount) > 0)
      throw new IllegalArgumentException("Insufficient funds: cannot withdraw more than the current balance");
    this.amount = this.amount.subtract(withdraw);
  }

  public boolean balanceAvailable(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException("Transfer amount must be positive and non-null");
    if (amount.compareTo(this.amount) > 0)
      throw new IllegalArgumentException("Insufficient funds: cannot transfer more than the current balance");
    return true;
  }
}
