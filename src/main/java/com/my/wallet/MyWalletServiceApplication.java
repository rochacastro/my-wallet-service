package com.my.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class MyWalletServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyWalletServiceApplication.class, args);
  }
}
