package com.jay.springbatch.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistrySmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

  /**
   * Automatically registers all Job beans in the JobRegistry
   * to enable name-based Job lookup and execution.
   */
  @Bean
  public JobRegistrySmartInitializingSingleton jobRegistrySmartInitializingSingleton(JobRegistry jobRegistry) {
    JobRegistrySmartInitializingSingleton registrar = new JobRegistrySmartInitializingSingleton();
    registrar.setJobRegistry(jobRegistry);
    return registrar;
  }

}
