package com.jay.springbatch.job.pass;

import com.jay.springbatch.repository.pass.*;
import com.jay.springbatch.repository.user.UserGroupMappingEntity;
import com.jay.springbatch.repository.user.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class AddPassesTaskletTest {

  @Mock
  private StepContribution stepContribution;
  @Mock
  private ChunkContext chunkContext;
  @Mock
  private PassRepository passRepository;
  @Mock
  private BulkPassRepository bulkPassRepository;
  @Mock
  private UserGroupMappingRepository userGroupMappingRepository;

  @InjectMocks
  private AddPassesTasklet addPassesTasklet;

  @Test
  public void test_execute() {
    // given
    final String userGroupId = "NVIDIA";
    final String userId = "A1000000";
    final Integer packageSeq = 1;
    final Integer count = 10;
    final LocalDateTime now = LocalDateTime.now();

    final BulkPassEntity bulkPassEntity = new BulkPassEntity();

    bulkPassEntity.setPackageSeq(packageSeq);
    bulkPassEntity.setUserGroupId(userGroupId);
    bulkPassEntity.setStatus(BulkPassStatus.READY);
    bulkPassEntity.setCount(count);
    bulkPassEntity.setStartedAt(now);
    bulkPassEntity.setEndedAt(now.plusDays(60));

    final UserGroupMappingEntity userGroupMappingEntity = new UserGroupMappingEntity();

    userGroupMappingEntity.setUserGroupId(userGroupId);
    userGroupMappingEntity.setUserId(userId);

    // when
    when(bulkPassRepository.findByStatusAndStartedAtAfter(eq(BulkPassStatus.READY), any()))
        .thenReturn(List.of(bulkPassEntity));
    when(userGroupMappingRepository.findByUserGroupId(eq("NVIDIA")))
        .thenReturn(List.of(userGroupMappingEntity));

    RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);

    // then
    assertEquals(RepeatStatus.FINISHED, repeatStatus);

    verify(passRepository, times(1)).saveAll(argThat((Collection<PassEntity> list) -> {
      assertEquals(1, list.size());
      PassEntity pass = list.iterator().next();
      assertAll(
          () -> assertEquals(packageSeq, pass.getPackageSeq()),
          () -> assertEquals(userId, pass.getUserId()),
          () -> assertEquals(PassStatus.READY, pass.getStatus()),
          () -> assertEquals(count, pass.getRemainingCount())
      );
      return true;
    }));
  }
}
