package com.jay.springbatch.repository.user;

import com.jay.springbatch.repository.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {

  @Id
  private String userId;

  @Column
  private String userName;

  @Enumerated(EnumType.STRING)
  @Column
  private UserStatus status;

  @Column
  private String phone;

  @Type(JsonStringType.class)
  @Column(columnDefinition = "text")
  private Map<String, Object> meta;

  public String getUuid() {
    String uuid = null;
    if (meta.containsKey("uuid")) {
      uuid = String.valueOf(meta.get("uuid"));
    }
    return uuid;
  }
}
