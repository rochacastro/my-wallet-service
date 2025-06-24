package my.wallet.com.vos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.br.CPF;

public record WalletTransferRequest(
    @CPF @NotBlank String from, @CPF @NotBlank String to, @NotNull @Positive BigDecimal amount) {}
