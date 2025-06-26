package com.my.wallet.configurations;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class HeaderInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String requestTraceIdValue = request.getHeader("requestTraceId");

    if (requestTraceIdValue == null || requestTraceIdValue.isBlank()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Header 'requestTraceId' is required");
      return false;
    }

    return true;
  }
}
