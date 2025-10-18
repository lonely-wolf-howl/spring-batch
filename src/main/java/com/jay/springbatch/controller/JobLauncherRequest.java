package com.jay.springbatch.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.Properties;

@Getter
@Setter
@ToString
public class JobLauncherRequest {

  private String name;
  private Properties jobParameters;

  public JobParameters getJobParameters() {
    JobParametersBuilder builder = new JobParametersBuilder();
    jobParameters.forEach((key, value) -> builder.addString(key.toString(), value.toString()));
    return builder.toJobParameters();
  }
}
