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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@StepScope
public class MakeWeeklyStatisticsTasklet implements Tasklet {

  private final String fromString;
  private final String toString;
  private final StatisticsRepository statisticsRepository;

  public MakeWeeklyStatisticsTasklet(StatisticsRepository statisticsRepository,
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

    Map<Integer, AggregatedStatistics> weeklyStatisticsEntityMap = new LinkedHashMap<>();

    for (AggregatedStatistics statistics : statisticsList) {
      int week = LocalDateTimeUtils.getWeekOfYear(statistics.getStatisticsAt());
      AggregatedStatistics savedStatisticsEntity = weeklyStatisticsEntityMap.get(week);

      if (savedStatisticsEntity == null) {
        weeklyStatisticsEntityMap.put(week, statistics);
      } else {
        savedStatisticsEntity.merge(statistics);
      }
    }

    List<String[]> rows = new ArrayList<>();
    rows.add(new String[]{"week", "allCount", "attendedCount", "cancelledCount"});

    weeklyStatisticsEntityMap.forEach((week, statistics) -> {
      rows.add(new String[]{
          "Week " + week,
          String.valueOf(statistics.getAllCount()),
          String.valueOf(statistics.getAttendedCount()),
          String.valueOf(statistics.getCancelledCount())
      });
    });

    final String fileName = "weekly_statistics_" +
        LocalDateTimeUtils.format(from, LocalDateTimeUtils.YYYY_MM_DD) + "_to_" +
        LocalDateTimeUtils.format(to, LocalDateTimeUtils.YYYY_MM_DD) + ".csv";
    CustomCSVWriter.write(fileName, rows);

    log.info("MakeWeeklyStatisticsTasklet - wrote weekly CSV: {}, weeks={}, window=[{}..{}]",
        fileName, rows.size(), from, to);

    return RepeatStatus.FINISHED;
  }
}
