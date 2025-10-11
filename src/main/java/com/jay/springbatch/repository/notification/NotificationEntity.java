package com.jay.springbatch.repository.notification;

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
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer notificationSeq;

  @Column
  private String uuid;

  @Enumerated(EnumType.STRING)
  private NotificationEvent event;

  @Column
  private String text;

  @Column
  private boolean sent;

  @Column
  private LocalDateTime sentAt;
}
