package com.jay.springbatch.repository.booking;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(name = "BookingEntity.updateUsedPass")
  int updateUsedPass(Integer passSeq, boolean usedPass);
}
