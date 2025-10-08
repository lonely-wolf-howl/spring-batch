package com.jay.springbatch.repository.pass;

import com.jay.springbatch.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "pass")
public class PassEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer passSeq;

  @Column
  private Integer packageSeq;

  @Column
  private String userId;

  @Enumerated(EnumType.STRING)
  @Column
  private PassStatus status;

  @Column
  private Integer remainingCount;

  @Column
  private LocalDateTime startedAt;

  @Column
  private LocalDateTime endedAt;

  @Column
  private LocalDateTime expiredAt;
}
