package com.jay.springbatch.job.notification;

import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.repository.booking.BookingStatus;
import com.jay.springbatch.repository.notification.NotificationEntity;
import com.jay.springbatch.repository.notification.NotificationEvent;
import com.jay.springbatch.repository.notification.NotificationModelMapper;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class SendNotificationBeforeClassJobConfig {

  private static final int CHUNK_SIZE = 10;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final SendNotificationItemWriter sendNotificationItemWriter;

  public SendNotificationBeforeClassJobConfig(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityManagerFactory,
      SendNotificationItemWriter sendNotificationItemWriter
  ) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
    this.entityManagerFactory = entityManagerFactory;
    this.sendNotificationItemWriter = sendNotificationItemWriter;
  }

  @Bean
  public Job sendNotificationBeforeClassJob() {
    return new JobBuilder("sendNotificationBeforeClassJob", jobRepository)
        .start(addNotificationStep())
        .next(sendNotificationStep())
        .build();
  }

  @Bean
  public Step addNotificationStep() {
    return new StepBuilder("addNotificationStep", jobRepository)
        .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE, transactionManager)
        .reader(addNotificationItemReader())
        .processor(addNotificationItemProcessor())
        .writer(addNotificationItemWriter())
        .build();
  }

  /**
   * This reader uses a paging approach that is thread-safe.
   */
  @Bean
  @StepScope
  public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
    return new JpaPagingItemReaderBuilder<BookingEntity>()
        .name("addNotificationItemReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(CHUNK_SIZE)
        .queryString("select b from BookingEntity b where b.status   = :status and b.startedAt <= :startedAt order by b.bookingSeq")
        .parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now().plusMinutes(10)))
        .build();
  }

  @Bean
  public ItemProcessor<BookingEntity, NotificationEntity> addNotificationItemProcessor() {
    return booking -> NotificationModelMapper.INSTANCE
        .toNotificationEntity(booking, NotificationEvent.BEFORE_CLASS);
  }

  @Bean
  public JpaItemWriter<NotificationEntity> addNotificationItemWriter() {
    return new JpaItemWriterBuilder<NotificationEntity>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }

  /**
   * - Reader is synchronized to ensure thread-safety with a cursor reader.
   * - Writer runs in parallel using a SimpleAsyncTaskExecutor.
   */
  @Bean
  public Step sendNotificationStep() {
    return new StepBuilder("sendNotificationStep", jobRepository)
        .<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE, transactionManager)
        .reader(sendNotificationItemReader())
        .writer(sendNotificationItemWriter)
        .taskExecutor(new SimpleAsyncTaskExecutor())
        .build();
  }

  /**
   * Cursor readers are not thread-safe by default.
   * Wrapping with this reader ensures only one thread interacts with the reader.
   */
  @Bean
  @StepScope
  public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() {
    JpaCursorItemReader<NotificationEntity> delegate = new JpaCursorItemReaderBuilder<NotificationEntity>()
        .name("sendNotificationItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("select n from NotificationEntity n where n.event = :event and n.sent  = :sent")
        .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
        .build();

    return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
        .delegate(delegate)
        .build();
  }
}
