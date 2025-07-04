package com.my.wallet.util;

import com.my.wallet.models.User;
import com.my.wallet.models.Wallet;
import java.math.BigDecimal;
import java.util.UUID;

public class UserBuild {

  public static User build(
      UUID userId, final String name, final String cpf, final BigDecimal amount) {
    final Wallet wallet = new Wallet();
    wallet.setId(UUID.randomUUID());
    User user = new User(userId, name, cpf, wallet);
    wallet.setUser(user);
    wallet.setAmount(amount);
    return user;
  }
}
