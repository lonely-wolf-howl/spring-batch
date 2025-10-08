package com.jay.springbatch.repository.pass;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PassRepository extends JpaRepository<PassEntity, Integer> {

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      UPDATE PassEntity pass
         SET pass.remainingCount = :remainingCount,
             pass.modifiedAt     = CURRENT_TIMESTAMP
       WHERE pass.passSeq        = :passSeq
      """)
  int updateRemainingCount(Integer passSeq, Integer remainingCount);
}
