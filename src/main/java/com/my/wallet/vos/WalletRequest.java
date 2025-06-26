package com.my.wallet.vos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.br.CPF;

public record WalletRequest(@CPF @NotBlank String cpf, @NotNull @Positive BigDecimal amount) {}
