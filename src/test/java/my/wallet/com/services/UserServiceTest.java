package my.wallet.com.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import my.wallet.com.mappers.UserMapper;
import my.wallet.com.models.User;
import my.wallet.com.repositories.UserRepository;
import my.wallet.com.util.UserBuild;
import my.wallet.com.vos.UserRequest;
import my.wallet.com.vos.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
  private static final String VALID_CPF = "26936761003";

  @Mock private UserRepository userRepository;

  @Mock private WalletService walletService;

  @Mock private UserMapper userMapper;

  @InjectMocks private UserService userService;

  @Test
  void saveUserWhenUserAlreadyExistsShouldUpdateNameOnly() {
    UUID uuid = UUID.randomUUID();

    UserRequest request = new UserRequest("João", VALID_CPF);
    User existingUser = UserBuild.build(uuid, "Antigo Nome", VALID_CPF, BigDecimal.TEN);

    User updatedUser = UserBuild.build(uuid, "João", VALID_CPF, BigDecimal.TEN);
    UserResponse expectedResponse = new UserResponse("João", VALID_CPF);

    when(userRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(existingUser)).thenReturn(updatedUser);
    when(userMapper.mapResponse(updatedUser)).thenReturn(expectedResponse);

    UserResponse result = userService.saveUser(request);

    assertEquals(expectedResponse, result);
    verify(walletService, never()).createWallet(any());
  }

  @Test
  void saveUserWhenUserDoesNotExistShouldCreateAndCallWallet() {
    UUID userId = UUID.randomUUID();

    UserRequest request = new UserRequest("Maria", VALID_CPF);
    User newUser = UserBuild.build(null, "Maria", VALID_CPF, BigDecimal.TEN);
    User savedUser = UserBuild.build(userId, "Maria", VALID_CPF, BigDecimal.TEN);
    UserResponse expectedResponse = new UserResponse("Maria", VALID_CPF);

    when(userRepository.findByCpf(VALID_CPF)).thenReturn(Optional.empty());
    when(userMapper.map(request)).thenReturn(newUser);
    when(userRepository.save(newUser)).thenReturn(savedUser);
    when(userMapper.mapResponse(savedUser)).thenReturn(expectedResponse);

    UserResponse result = userService.saveUser(request);

    assertEquals(expectedResponse, result);
    verify(walletService).createWallet(savedUser);
  }

  @Test
  void findUserByCpfWhenUserExistsShouldReturnUser() {

    User user = UserBuild.build(UUID.randomUUID(), "Carlos", VALID_CPF, BigDecimal.TEN);
    when(userRepository.findByCpf(VALID_CPF)).thenReturn(Optional.of(user));

    User result = userService.findUserByCpf(VALID_CPF);

    assertEquals(user, result);
  }

  @Test
  void findUserByCpfWhenUserDoesNotExistShouldThrowException() {
    when(userRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

    EntityNotFoundException ex =
        assertThrows(
            EntityNotFoundException.class,
            () -> {
              userService.findUserByCpf("00000000000");
            });

    assertEquals("User not found", ex.getMessage());
  }
}
