package com.jay.springbatch.repository.pass;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PassRepository extends JpaRepository<PassEntity, Integer> {

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(name = "PassEntity.updateRemainingCount")
  int updateRemainingCount(Integer passSeq, Integer remainingCount);
}
