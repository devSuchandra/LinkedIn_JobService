package com.CodingShuttle.JobService.Entity;

import com.CodingShuttle.JobService.Entity.Enum.ExperienceLevel;
import com.CodingShuttle.JobService.Entity.Enum.JobStatus;
import com.CodingShuttle.JobService.Entity.Enum.WorkMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "jobs", indexes = {
        @Index(name = "idx_jobs_job_status", columnList = "job_status"),
        @Index(name = "idx_jobs_location", columnList = "location"),
        @Index(name = "idx_jobs_company_id", columnList = "company_id"),
        @Index(name = "idx_jobs_experience_level", columnList = "experience_level"),
        @Index(name = "idx_jobs_work_mode", columnList = "work_mode"),
        @Index(name = "idx_jobs_created_at", columnList = "created_at"),
})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false)
    private Long companyId;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExperienceLevel experienceLevel;

    @Column(nullable = false)
    private Integer minRequiredExperienceYears;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(length = 3)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus jobStatus = JobStatus.OPEN;

    @Column(nullable = false)
    private Long recruiterId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    //A job is considered active if it is OPEN and not expired.
    public boolean isAcceptingApplications(LocalDateTime now) {
        if(jobStatus != JobStatus.OPEN) {
            return false;
        }
        return expiresAt == null || expiresAt.isAfter(now);
    }
}
