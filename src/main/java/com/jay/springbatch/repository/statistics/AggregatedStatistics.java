package com.jay.springbatch.repository.statistics;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AggregatedStatistics {

  @Column
  private LocalDateTime statisticsAt;

  @Column
  private long allCount;

  @Column
  private long attendedCount;

  @Column
  private long cancelledCount;

  public void merge(final AggregatedStatistics statistics) {
    this.allCount += statistics.getAllCount();
    this.attendedCount += statistics.getAttendedCount();
    this.cancelledCount += statistics.getCancelledCount();
  }
}
