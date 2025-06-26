package com.my.wallet.controllers;

import com.my.wallet.services.WalletService;
import com.my.wallet.vos.WalletBalance;
import com.my.wallet.vos.WalletRequest;
import com.my.wallet.vos.WalletTransferRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

  private final WalletService service;

  public WalletController(final WalletService service) {
    this.service = service;
  }

  @PatchMapping(value = "/v1/deposit")
  public ResponseEntity<Void> depositByCpf(final @RequestBody @Valid WalletRequest walletRequest) {
    service.depositAmount(walletRequest);
    return ResponseEntity.ok().build();
  }

  @PatchMapping(value = "/v1/withdraw")
  public ResponseEntity<Void> withdrawByCpf(final @RequestBody @Valid WalletRequest walletRequest) {
    service.withdrawAmount(walletRequest);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/v1/balance")
  public ResponseEntity<WalletBalance> balance(@NotBlank @CPF final String cpf) {
    WalletBalance userBalance = service.getUserBalance(cpf);
    return ResponseEntity.ok(userBalance);
  }

  @GetMapping(value = "/v1/historical-balance")
  public ResponseEntity<WalletBalance> historicalBalance(
      @NotBlank @CPF final String cpf, @NotNull LocalDateTime date) {
    WalletBalance userHistoricalBalance = service.getUserHistoricalBalance(cpf, date);
    return ResponseEntity.ok(userHistoricalBalance);
  }

  @PatchMapping(value = "/v1/transfer")
  public ResponseEntity<Void> transferAmount(
      final @RequestBody @Valid WalletTransferRequest walletTransferRequest) {
    service.transferAmount(walletTransferRequest);
    return ResponseEntity.ok().build();
  }
}
