package com.my.wallet.controllers;

import com.my.wallet.services.UserService;
import com.my.wallet.vos.UserRequest;
import com.my.wallet.vos.UserResponse;
import jakarta.validation.Valid;
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
