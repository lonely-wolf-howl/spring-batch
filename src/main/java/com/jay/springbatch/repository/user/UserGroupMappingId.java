package com.jay.springbatch.repository.user;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UserGroupMappingId implements Serializable {

  @Column
  private String userGroupId;

  @Column
  private String userId;
}
