package com.jay.springbatch.repository.passPackage;

import com.jay.springbatch.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "package")
public class PackageEntity extends BaseEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer packageSeq;

  @Column
  private String packageName;

  @Column
  private Integer count;

  @Column
  private Integer period;
}
