package com.my.wallet.services;

import com.my.wallet.mappers.UserMapper;
import com.my.wallet.models.User;
import com.my.wallet.repositories.UserRepository;
import com.my.wallet.vos.UserRequest;
import com.my.wallet.vos.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository repository;
  private final WalletService walletService;
  private final UserMapper mapper;

  public UserService(
      UserRepository repository, final UserMapper mapper, final WalletService walletService) {
    this.repository = repository;
    this.mapper = mapper;
    this.walletService = walletService;
  }

  public UserResponse saveUser(UserRequest userRequest) {
    Optional<User> byCpf = repository.findByCpf(userRequest.cpf());

    User response;
    if (byCpf.isPresent()) {
      User userSaved = byCpf.get();
      userSaved.setName(userRequest.name());
      response = repository.save(userSaved);
    } else {
      User user = repository.save(mapper.map(userRequest));
      walletService.createWallet(user);
      response = user;
    }

    return mapper.mapResponse(response);
  }

  public User findUserByCpf(String cpf) {
    return repository
        .findByCpf(cpf)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }
}
