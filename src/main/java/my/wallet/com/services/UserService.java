package my.wallet.com.services;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import my.wallet.com.mappers.UserMapper;
import my.wallet.com.models.User;
import my.wallet.com.repositories.UserRepository;
import my.wallet.com.vos.UserRequest;
import my.wallet.com.vos.UserResponse;
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
