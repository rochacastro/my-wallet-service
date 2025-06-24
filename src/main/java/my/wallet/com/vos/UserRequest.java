package my.wallet.com.vos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;

public record UserRequest(@NotEmpty String name, @NotBlank @CPF String cpf) {}
