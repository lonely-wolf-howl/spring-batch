package com.jay.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchApplication {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Autowired
  public SpringBatchApplication(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
  }

  @Bean
  public Step passStep() {
    return new StepBuilder("passStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          System.out.println("Execute PassStep");
          return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
  }

  @Bean
  public Job passJob() {
    return new JobBuilder("passJob", jobRepository)
        .start(passStep())
        .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchApplication.class, args);
  }
}
