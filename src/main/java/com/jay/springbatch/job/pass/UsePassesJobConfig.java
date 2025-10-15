package com.jay.springbatch.job.pass;

import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.repository.booking.BookingRepository;
import com.jay.springbatch.repository.booking.BookingStatus;
import com.jay.springbatch.repository.pass.PassEntity;
import com.jay.springbatch.repository.pass.PassRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
public class UsePassesJobConfig {

  private static final int CHUNK_SIZE = 5;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final PassRepository passRepository;
  private final BookingRepository bookingRepository;

  public UsePassesJobConfig(JobRepository jobRepository,
                            PlatformTransactionManager platformTransactionManager,
                            EntityManagerFactory entityManagerFactory,
                            PassRepository passRepository,
                            BookingRepository bookingRepository) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.entityManagerFactory = entityManagerFactory;
    this.passRepository = passRepository;
    this.bookingRepository = bookingRepository;
  }

  @Bean
  public Job usePassesJob() {
    return new JobBuilder("usePassesJob", jobRepository)
        .start(usePassesStep())
        .build();
  }

  @Bean
  public Step usePassesStep() {
    return new StepBuilder("usePassesStep", jobRepository)
        .<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE, platformTransactionManager)
        .reader(usePassesItemReader())
        .processor(usePassesAsyncItemProcessor())
        .writer(usePassesAsyncItemWriter())
        .build();
  }

  @Bean
  public JpaCursorItemReader<BookingEntity> usePassesItemReader() {
    return new JpaCursorItemReaderBuilder<BookingEntity>()
        .name("usePassesItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
        .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
        .build();
  }

  /**
   * This processor delegates item processing to {@code usePassesItemProcessor()},
   * and executes each item 'asynchronously' in parallel threads managed by {@link SimpleAsyncTaskExecutor}.
   * The delegate's results are wrapped in {@code Future} objects for deferred completion.
   * If the delegate processor is lightweight, enabling async processing may introduce unnecessary overhead.
   */
  @Bean
  public AsyncItemProcessor<BookingEntity, BookingEntity> usePassesAsyncItemProcessor() {
    AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor = new AsyncItemProcessor<>();
    asyncItemProcessor.setDelegate(usePassesItemProcessor());
    asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

    return asyncItemProcessor;
  }

  @Bean
  public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {
    return bookingEntity -> {
      PassEntity passEntity = bookingEntity.getPassEntity();
      passEntity.setRemainingCount(passEntity.getRemainingCount() - 1);
      bookingEntity.setPassEntity(passEntity);

      bookingEntity.setUsedPass(true);

      return bookingEntity;
    };
  }

  /**
   * This writer collects a list of {@code Future<BookingEntity>} objects produced by the async processor,
   * blocks until all futures complete, retrieves the resolved items,
   * and 'synchronously' delegates them to {@link #usePassesItemWriter()} for final write operations.
   */
  @Bean
  public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter() {
    AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
    asyncItemWriter.setDelegate(usePassesItemWriter());

    return asyncItemWriter;
  }

  @Bean
  public ItemWriter<BookingEntity> usePassesItemWriter() {
    return bookingEntities -> {
      for (BookingEntity bookingEntity : bookingEntities) {
        int updatedCount = passRepository.updateRemainingCount(bookingEntity.getPassSeq(), bookingEntity.getPassEntity().getRemainingCount());
        if (updatedCount > 0) {
          bookingRepository.updateUsedPass(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
        }
      }
    };
  }
}
