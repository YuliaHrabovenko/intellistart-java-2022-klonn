package interviewplanning.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebConfig class.
 */
@Configuration
public class WebConfig {
  /**
   * Get model mapper.
   *
   * @return model mapper
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    return modelMapper;
  }
}
