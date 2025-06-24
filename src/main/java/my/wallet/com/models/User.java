package my.wallet.com.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue private UUID id;

  @NotBlank private String name;

  @NotBlank
  @Column(unique = true)
  private String cpf;

  @OneToOne(mappedBy = "user")
  private Wallet wallet;

  public User() {}

  public User(UUID id, String name, String cpf, Wallet wallet) {
    this.id = id;
    this.name = name;
    this.cpf = cpf;
    this.wallet = wallet;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public Wallet getWallet() {
    return wallet;
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }
}
