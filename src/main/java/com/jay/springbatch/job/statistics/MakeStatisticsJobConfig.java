package com.jay.springbatch.job.statistics;

import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.repository.statistics.StatisticsEntity;
import com.jay.springbatch.repository.statistics.StatisticsRepository;
import com.jay.springbatch.util.LocalDateTimeUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class MakeStatisticsJobConfig {

  private static final int CHUNK_SIZE = 5;

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final StatisticsRepository statisticsRepository;
  private final MakeDailyStatisticsTasklet makeDailyStatisticsTasklet;
  private final MakeWeeklyStatisticsTasklet makeWeeklyStatisticsTasklet;

  public MakeStatisticsJobConfig(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 EntityManagerFactory entityManagerFactory,
                                 StatisticsRepository statisticsRepository,
                                 MakeDailyStatisticsTasklet makeDailyStatisticsTasklet,
                                 MakeWeeklyStatisticsTasklet makeWeeklyStatisticsTasklet) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = transactionManager;
    this.entityManagerFactory = entityManagerFactory;
    this.statisticsRepository = statisticsRepository;
    this.makeDailyStatisticsTasklet = makeDailyStatisticsTasklet;
    this.makeWeeklyStatisticsTasklet = makeWeeklyStatisticsTasklet;
  }

  @Bean
  public Job makeStatisticsJob() {
    Flow addStatisticsFlow = new FlowBuilder<Flow>("addStatisticsFlow")
        .start(addStatisticsStep())
        .end();

    Flow makeDailyStatisticsFlow = new FlowBuilder<Flow>("makeDailyStatisticsFlow")
        .start(makeDailyStatisticsStep())
        .end();

    Flow makeWeeklyStatisticsFlow = new FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
        .start(makeWeeklyStatisticsStep())
        .end();

    Flow parallelMakeStatisticsFlow = new FlowBuilder<Flow>("parallelMakeStatisticsFlow")
        .split(new SimpleAsyncTaskExecutor())
        .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
        .end();

    return new JobBuilder("makeStatisticsJob", jobRepository)
        .start(addStatisticsFlow)
        .next(parallelMakeStatisticsFlow)
        .end()
        .build();
  }

  @Bean
  public Step addStatisticsStep() {
    return new StepBuilder("addStatisticsStep", jobRepository)
        .<BookingEntity, BookingEntity>chunk(CHUNK_SIZE, platformTransactionManager)
        .reader(addStatisticsItemReader(null, null))
        .writer(addStatisticsItemWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaCursorItemReader<BookingEntity> addStatisticsItemReader(
      @Value("#{jobParameters[from]}") String fromString,
      @Value("#{jobParameters[to]}") String toString
  ) {
    final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
    final LocalDateTime to = LocalDateTimeUtils.parse(toString);

    return new JpaCursorItemReaderBuilder<BookingEntity>()
        .name("addStatisticsItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("select b from BookingEntity b where b.endedAt between :from and :to")
        .parameterValues(Map.of("from", from, "to", to))
        .build();
  }

  @Bean
  public ItemWriter<BookingEntity> addStatisticsItemWriter() {
    return bookingEntities -> {
      Map<LocalDateTime, StatisticsEntity> statisticsEntityMap = new LinkedHashMap<>();

      for (BookingEntity bookingEntity : bookingEntities) {
        final LocalDateTime statisticsAt = bookingEntity.getStatisticsAt();
        StatisticsEntity statisticsEntity = statisticsEntityMap.get(statisticsAt);

        if (statisticsEntity == null) {
          statisticsEntityMap.put(statisticsAt, StatisticsEntity.create(bookingEntity));
        } else {
          statisticsEntity.add(bookingEntity);
        }
      }

      final List<StatisticsEntity> statisticsEntities = new ArrayList<>(statisticsEntityMap.values());
      statisticsRepository.saveAll(statisticsEntities);
    };
  }

  @Bean
  public Step makeDailyStatisticsStep() {
    return new StepBuilder("makeDailyStatisticsStep", jobRepository)
        .tasklet(makeDailyStatisticsTasklet, platformTransactionManager)
        .build();
  }

  @Bean
  public Step makeWeeklyStatisticsStep() {
    return new StepBuilder("makeWeeklyStatisticsStep", jobRepository)
        .tasklet(makeWeeklyStatisticsTasklet, platformTransactionManager)
        .build();
  }
}
