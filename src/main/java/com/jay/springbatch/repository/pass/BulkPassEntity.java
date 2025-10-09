package com.jay.springbatch.repository.pass;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "bulk_pass")
public class BulkPassEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer bulkPassSeq;

  @Column
  private Integer packageSeq;

  @Column
  private String userGroupId;

  @Enumerated(EnumType.STRING)
  @Column
  private BulkPassStatus status;

  @Column
  private Integer count;

  @Column
  private LocalDateTime startedAt;

  @Column
  private LocalDateTime endedAt;
}
