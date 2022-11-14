package com.intellias.intellistart.interviewplanning.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebConfig class.
 */
@Configuration
public class WebConfig {
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
