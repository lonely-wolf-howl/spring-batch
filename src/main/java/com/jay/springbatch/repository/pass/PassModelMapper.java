package com.jay.springbatch.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassModelMapper {

  PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);

  /**
   * if field names are different or custom mapping is required,
   * add '@Mapping' annotation
   */
  @Mapping(target = "status", qualifiedByName = "defaultStatus")
  @Mapping(target = "remainingCount", source = "bulkPassEntity.count")
  PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId);

  /**
   * set 'PassStatus' value regardless of 'BulkPassStatus'
   */
  @Named("defaultStatus")
  default PassStatus status(BulkPassStatus status) {
    return PassStatus.READY;
  }
}
