package com.jay.springbatch.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AddPassesJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final AddPassesTasklet addPassesTasklet;

  public AddPassesJobConfig(JobRepository jobRepository,
                            PlatformTransactionManager platformTransactionManager,
                            AddPassesTasklet addPassesTasklet) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.addPassesTasklet = addPassesTasklet;
  }

  @Bean
  public Job addPassesJob(Step addPassesStep) {
    return new JobBuilder("addPassesJob", jobRepository)
        .start(addPassesStep)
        .build();
  }

  @Bean
  public Step addPassesStep() {
    return new StepBuilder("addPassesStep", jobRepository)
        .tasklet(addPassesTasklet, platformTransactionManager)
        .build();
  }
}