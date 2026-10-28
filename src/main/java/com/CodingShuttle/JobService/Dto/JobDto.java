package com.CodingShuttle.JobService.Dto;

import com.CodingShuttle.JobService.Entity.Enum.ExperienceLevel;
import com.CodingShuttle.JobService.Entity.Enum.JobStatus;
import com.CodingShuttle.JobService.Entity.Enum.WorkMode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private Long companyId;
    private String location;
    private WorkMode workMode;
    private ExperienceLevel experienceLevel;
    private Integer minRequiredExperienceYears;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currencyCode;
    private JobStatus jobStatus;
    private Long recruiterId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
