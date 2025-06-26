package my.wallet.com.controllers;

import jakarta.validation.Valid;
import my.wallet.com.services.UserService;
import my.wallet.com.vos.UserRequest;
import my.wallet.com.vos.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(
      final @RequestBody @Valid UserRequest userRequest) {
    UserResponse userResponse = userService.saveUser(userRequest);
    return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
  }
}
