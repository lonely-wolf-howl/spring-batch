package com.jay.springbatch.job.pass;

import com.jay.springbatch.repository.pass.PassEntity;
import com.jay.springbatch.repository.pass.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassesJobConfig {

  private static final int CHUNK_SIZE = 5;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;

  public ExpirePassesJobConfig(JobRepository jobRepository,
                               PlatformTransactionManager platformTransactionManager,
                               EntityManagerFactory entityManagerFactory) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public Job expirePassesJob(Step expirePassesStep) {
    return new JobBuilder("expirePassesJob", jobRepository)
        .start(expirePassesStep)
        .build();
  }

  @Bean
  public Step expirePassesStep(JpaCursorItemReader<PassEntity> expirePassesItemReader,
                               ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor,
                               JpaItemWriter<PassEntity> expirePassesItemWriter) {
    return new StepBuilder("expirePassesStep", jobRepository)
        .<PassEntity, PassEntity>chunk(CHUNK_SIZE, platformTransactionManager)
        .reader(expirePassesItemReader)
        .processor(expirePassesItemProcessor)
        .writer(expirePassesItemWriter)
        .build();
  }

  /**
   * This reader uses a cursor-based approach that offers higher performance,
   * and ensures data consistency unaffected by concurrent updates, unlike paging.
   * However, its drawback is that if the job is interrupted mid-process,
   * it cannot easily resume from the exact point of failure, and transaction rollbacks can be costly.
   */
  @Bean
  @StepScope
  public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
    return new JpaCursorItemReaderBuilder<PassEntity>()
        .name("expirePassesItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
        .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
        .build();
  }

  @Bean
  public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
    return pass -> {
      pass.setStatus(PassStatus.EXPIRED);
      pass.setExpiredAt(LocalDateTime.now());

      return pass;
    };
  }

  @Bean
  public JpaItemWriter<PassEntity> expirePassesItemWriter() {
    return new JpaItemWriterBuilder<PassEntity>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
