package com.jay.springbatch.repository.user;

import com.jay.springbatch.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class)
public class UserGroupMappingEntity extends BaseEntity {

  @Id
  private String userGroupId;

  @Id
  private String userId;

  @Column
  private String userGroupName;

  @Column
  private String description;
}
