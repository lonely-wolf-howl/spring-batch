package com.jay.springbatch.repository.statistics;

import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.repository.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer statisticsSeq;

  @Column
  private LocalDateTime statisticsAt;

  @Column
  private int allCount;

  @Column
  private int attendedCount;

  @Column
  private int cancelledCount;

  public static StatisticsEntity create(final BookingEntity bookingEntity) {
    StatisticsEntity statisticsEntity = new StatisticsEntity();
    statisticsEntity.setStatisticsAt(bookingEntity.getStatisticsAt());
    
    // step 1.
    statisticsEntity.setAllCount(1);
    // step 2.
    if (bookingEntity.isAttended()) {
      statisticsEntity.setAttendedCount(1);
    }
    // step 3.
    if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
      statisticsEntity.setCancelledCount(1);
    }

    return statisticsEntity;
  }

  public void add(final BookingEntity bookingEntity) {
    this.allCount++;
    if (bookingEntity.isAttended()) {
      this.attendedCount++;
    }
    if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
      this.cancelledCount++;
    }
  }
}
