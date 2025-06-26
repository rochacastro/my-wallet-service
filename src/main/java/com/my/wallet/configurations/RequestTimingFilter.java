package com.my.wallet.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestTimingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(RequestTimingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, jakarta.servlet.ServletException {

    String uri = request.getRequestURI();

    long startTime = System.currentTimeMillis();

    try {
      filterChain.doFilter(request, response);
    } finally {
      long duration = System.currentTimeMillis() - startTime;

      String method = request.getMethod();
      int status = response.getStatus();
      String requestTraceId = request.getHeader("requestTraceId");

      logger.info(
          "Method={}, uri={}, status={}, duration={}, requestTraceId={}",
          method,
          uri,
          status,
          duration,
          requestTraceId);
    }
  }
}
