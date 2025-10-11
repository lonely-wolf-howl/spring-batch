package com.jay.springbatch.repository.booking;

import com.jay.springbatch.repository.BaseEntity;
import com.jay.springbatch.repository.pass.PassEntity;
import com.jay.springbatch.repository.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer bookingSeq;

  @Column
  private Integer passSeq;

  @Column
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column
  private BookingStatus status;

  @Column
  private boolean usedPass;

  @Column
  private boolean attended;

  @Column
  private LocalDateTime startedAt;

  @Column
  private LocalDateTime endedAt;

  @Column
  private LocalDateTime cancelledAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "passSeq", insertable = false, updatable = false)
  private PassEntity passEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", insertable = false, updatable = false)
  private UserEntity userEntity;
}
