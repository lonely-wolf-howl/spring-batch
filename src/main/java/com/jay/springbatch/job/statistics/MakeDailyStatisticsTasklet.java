package com.jay.springbatch.job.statistics;

import com.jay.springbatch.repository.statistics.AggregatedStatistics;
import com.jay.springbatch.repository.statistics.StatisticsRepository;
import com.jay.springbatch.util.CustomCSVWriter;
import com.jay.springbatch.util.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@StepScope
public class MakeDailyStatisticsTasklet implements Tasklet {

  private final String fromString;
  private final String toString;
  private final StatisticsRepository statisticsRepository;

  public MakeDailyStatisticsTasklet(StatisticsRepository statisticsRepository,
                                    @Value("#{jobParameters[from]}") String fromString,
                                    @Value("#{jobParameters[to]}") String toString) {
    this.statisticsRepository = statisticsRepository;
    this.fromString = fromString;
    this.toString = toString;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    final LocalDateTime from = LocalDateTimeUtils.parse(fromString);
    final LocalDateTime to = LocalDateTimeUtils.parse(toString);

    final List<AggregatedStatistics> statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to);

    List<String[]> rows = new ArrayList<>();
    rows.add(new String[]{"statisticsAt", "allCount", "attendedCount", "cancelledCount"});
    
    for (AggregatedStatistics statistics : statisticsList) {
      rows.add(new String[]{
          LocalDateTimeUtils.format(statistics.getStatisticsAt()),
          String.valueOf(statistics.getAllCount()),
          String.valueOf(statistics.getAttendedCount()),
          String.valueOf(statistics.getCancelledCount())
      });
    }

    final String fileName = "daily_statistics_" +
        LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + "_to_" +
        LocalDateTimeUtils.format(to, LocalDateTimeUtils.YYYY_MM_DD) + ".csv";
    CustomCSVWriter.write(fileName, rows);

    log.info("MakeDailyStatisticsTasklet - wrote daily CSV: {}, rows={}, window=[{}..{}]",
        fileName, rows.size() - 1, from, to);

    return RepeatStatus.FINISHED;
  }
}
