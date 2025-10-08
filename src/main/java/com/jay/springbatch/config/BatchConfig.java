package com.jay.springbatch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistrySmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

  @Bean
  public JobRegistrySmartInitializingSingleton jobRegistryInitializer(JobRegistry jobRegistry) {
    return new JobRegistrySmartInitializingSingleton(jobRegistry);
  }
  
}
