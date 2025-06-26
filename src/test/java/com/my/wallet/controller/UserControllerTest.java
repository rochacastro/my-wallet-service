package com.my.wallet.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.wallet.controllers.UserController;
import com.my.wallet.services.UserService;
import com.my.wallet.vos.UserRequest;
import com.my.wallet.vos.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
  private static final String PATH = "/api/v1/user";
  private static final String VALID_CPF = "27175250096";
  private static final String INVALID_CPF = "27175250196";

  private static final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;

  @Test
  void shouldCreateUserSuccessfully() throws Exception {
    UserRequest userRequest = new UserRequest("João", VALID_CPF);

    UserResponse userResponse = new UserResponse("João", VALID_CPF);

    Mockito.when(userService.saveUser(any(UserRequest.class))).thenReturn(userResponse);

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("requestTraceId", "teste123"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("João"))
        .andExpect(jsonPath("$.cpf").value(VALID_CPF));

    verify(userService).saveUser(any(UserRequest.class));
  }

  @Test
  void shouldValidateCpfReturnError() throws Exception {
    UserRequest userRequest = new UserRequest("João", INVALID_CPF);

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("requestTraceId", "teste123"))
        .andExpect(status().isBadRequest());

    verify(userService, never()).saveUser(any(UserRequest.class));
  }

  @Test
  void shouldValidateEmptyNameReturnError() throws Exception {
    UserRequest userRequest = new UserRequest("", INVALID_CPF);

    mockMvc
        .perform(
            post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
                .header("requestTraceId", "teste123"))
        .andExpect(status().isBadRequest());

    verify(userService, never()).saveUser(any(UserRequest.class));
  }
}
