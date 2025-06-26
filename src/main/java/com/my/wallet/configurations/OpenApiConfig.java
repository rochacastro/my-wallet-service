package com.my.wallet.configurations;

import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public GlobalOpenApiCustomizer globalHeaderOpenApiCustomiser() {
    return openApi ->
        openApi
            .getPaths()
            .values()
            .forEach(
                pathItem ->
                    pathItem
                        .readOperations()
                        .forEach(
                            operation ->
                                operation.addParametersItem(
                                    new HeaderParameter()
                                        .name("requestTraceId")
                                        .description("Request trace Id header")
                                        .required(true)
                                        .example("12345"))));
  }
}
