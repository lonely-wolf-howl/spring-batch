package com.jay.springbatch.repository;

import com.jay.springbatch.repository.passPackage.PackageEntity;
import com.jay.springbatch.repository.passPackage.PackageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PackageRepositoryTest {

  @Autowired
  private PackageRepository packageRepository;

  @Test
  public void save() {
    // given
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setPackageName("package 4 weeks");
    packageEntity.setPeriod(28);

    // when
    packageRepository.save(packageEntity);

    // then
    assertNotNull(packageEntity.getPackageSeq());
  }

  @Test
  public void findByCreatedAtAfter() {
    // given
    LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);

    PackageEntity packageEntity1 = new PackageEntity();
    packageEntity1.setPackageName("package 4 weeks");
    packageEntity1.setPeriod(28);
    packageRepository.save(packageEntity1);

    PackageEntity packageEntity2 = new PackageEntity();
    packageEntity2.setPackageName("package 8 weeks");
    packageEntity2.setPeriod(56);
    packageRepository.save(packageEntity2);

    // when
    final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("packageSeq").descending()));

    // then
    assertEquals(1, packageEntities.size());
    assertEquals(packageEntity2.getPackageSeq(), packageEntities.getFirst().getPackageSeq());
  }

  @Test
  public void updatePeriod() {
    // given
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setPackageName("package 8 weeks");
    packageEntity.setPeriod(28);
    packageRepository.save(packageEntity);

    // when
    int updatedCount = packageRepository.updatePeriod(packageEntity.getPackageSeq(), 56);
    final PackageEntity updatedPackageEntity = packageRepository.findById(packageEntity.getPackageSeq())
        .orElseThrow(() -> new IllegalArgumentException("Package not found: " + packageEntity.getPackageSeq()));

    // then
    assertEquals(1, updatedCount);
    assertEquals(56, updatedPackageEntity.getPeriod());
  }

  @Test
  public void delete() {
    // given
    PackageEntity packageEntity = new PackageEntity();
    packageEntity.setPackageName("package 2 weeks");
    packageEntity.setCount(4);
    PackageEntity newPackageEntity = packageRepository.save(packageEntity);

    // when
    packageRepository.deleteById(newPackageEntity.getPackageSeq());

    // then
    assertTrue(packageRepository.findById(newPackageEntity.getPackageSeq()).isEmpty());
  }
}
