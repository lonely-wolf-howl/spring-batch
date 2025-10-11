package com.jay.springbatch.repository.notification;

import com.jay.springbatch.repository.booking.BookingEntity;
import com.jay.springbatch.util.LocalDateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationModelMapper {

  NotificationModelMapper INSTANCE = Mappers.getMapper(NotificationModelMapper.class);

  @Mapping(target = "uuid", source = "bookingEntity.userEntity.uuid")
  @Mapping(target = "text", source = "bookingEntity.startedAt", qualifiedByName = "text")
  NotificationEntity toNotificationEntity(BookingEntity bookingEntity, NotificationEvent event);

  @Named("text")
  default String text(LocalDateTime startedAt) {
    return String.format(
        "Hello! Your class at %s is starting. Please check in before class. \uD83D\uDE0A",
        LocalDateTimeUtils.format(startedAt)
    );
  }
}
