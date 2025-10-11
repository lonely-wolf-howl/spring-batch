package com.jay.springbatch.job.notification;

import com.jay.springbatch.adapter.message.KakaoTalkMessageAdapter;
import com.jay.springbatch.repository.notification.NotificationEntity;
import com.jay.springbatch.repository.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {

  private final NotificationRepository notificationRepository;
  private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

  public SendNotificationItemWriter(NotificationRepository notificationRepository,
                                    KakaoTalkMessageAdapter kakaoTalkMessageAdapter) {
    this.notificationRepository = notificationRepository;
    this.kakaoTalkMessageAdapter = kakaoTalkMessageAdapter;
  }

  @Override
  public void write(Chunk<? extends NotificationEntity> chunk) throws Exception {
    int sent = 0;

    for (NotificationEntity n : chunk) {
      boolean ok = kakaoTalkMessageAdapter.sendKakaoTalkMessage(n.getUuid(), n.getText());
      if (ok) {
        n.setSent(true);
        n.setSentAt(LocalDateTime.now());
        notificationRepository.save(n);
        sent++;
      }
    }

    log.info("SendNotificationItemWriter - write: sent {}/{} notifications before class",
        sent, chunk.size());
  }
}
