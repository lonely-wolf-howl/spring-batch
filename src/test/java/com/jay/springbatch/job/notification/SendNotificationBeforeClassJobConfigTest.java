package com.jay.springbatch.job.notification;

import com.jay.springbatch.adapter.message.KakaoTalkMessageAdapter;
import com.jay.springbatch.config.KakaoTalkMessageConfig;
import com.jay.springbatch.config.TestBatchConfig;
import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.repository.booking.BookingRepository;
import com.jay.springbatch.repository.booking.BookingStatus;
import com.jay.springbatch.repository.pass.PassEntity;
import com.jay.springbatch.repository.pass.PassRepository;
import com.jay.springbatch.repository.pass.PassStatus;
import com.jay.springbatch.repository.user.UserEntity;
import com.jay.springbatch.repository.user.UserRepository;
import com.jay.springbatch.repository.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBatchTest
@SpringBootTest(classes = {
    SendNotificationBeforeClassJobConfig.class,
    TestBatchConfig.class,
    SendNotificationItemWriter.class,
    KakaoTalkMessageConfig.class,
    KakaoTalkMessageAdapter.class
})
@ActiveProfiles("test")
public class SendNotificationBeforeClassJobConfigTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PassRepository passRepository;
  @Autowired
  private BookingRepository bookingRepository;

  @Test
  public void test_addNotificationStep() throws Exception {
    // given
    addBookingEntity();

    // when
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("addNotificationStep");

    // then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
  }

  private void addBookingEntity() {
    final LocalDateTime now = LocalDateTime.now();
    final String userId = "A100" + RandomStringUtils.secure().nextNumeric(4);

    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(userId);
    userEntity.setUserName("Emily Johnson");
    userEntity.setStatus(UserStatus.ACTIVE);
    userEntity.setPhone("01024115823");
    userEntity.setMeta(Map.of("uuid", UUID.randomUUID().toString()));
    userRepository.save(userEntity);

    PassEntity passEntity = new PassEntity();
    passEntity.setPackageSeq(1);
    passEntity.setUserId(userId);
    passEntity.setStatus(PassStatus.PROGRESSED);
    passEntity.setRemainingCount(10);
    passEntity.setStartedAt(now.minusDays(30));
    passEntity.setEndedAt(now.minusDays(1));
    passRepository.save(passEntity);

    BookingEntity bookingEntity = new BookingEntity();
    bookingEntity.setPassSeq(passEntity.getPassSeq());
    bookingEntity.setUserId(userId);
    bookingEntity.setStatus(BookingStatus.READY);
    bookingEntity.setStartedAt(now.plusMinutes(60));
    bookingEntity.setEndedAt(bookingEntity.getStartedAt().plusMinutes(30));
    bookingRepository.save(bookingEntity);
  }
}
