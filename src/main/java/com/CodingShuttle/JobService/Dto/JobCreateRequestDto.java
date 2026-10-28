package com.CodingShuttle.JobService.Dto;

import com.CodingShuttle.JobService.Entity.Enum.ExperienceLevel;
import com.CodingShuttle.JobService.Entity.Enum.WorkMode;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobCreateRequestDto {

    @NotBlank(message = "must not be blank")
    @Size(max = 150, message = "must be at most 150 characters")
    private String title;

    @NotBlank(message = "must not be blank")
    @Size(max = 150, message = "must be at most 150 characters")
    private String companyName;

    @NotNull(message = "must not be null")
    @Positive(message = "must be greater than 0")
    private Long companyId;

    @NotBlank(message = "must not be blank")
    @Size(max = 120, message = "must be at most 120 characters")
    private String location;

    @Size(max = 5000, message = "must be at most 5000 characters")
    private String description;

    @NotNull(message = "must be one of ONSITE, HYBRID, REMOTE")
    private WorkMode workMode;

    @NotNull(message = "must be one of INTERN, ENTRY, MID, SENIOR, LEAD, EXECUTIVE")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "must not be null")
    @Min(value = 0, message = "must not be negative")
    @Max(value = 60, message = "must be at most 60")
    private Integer minRequiredExperienceYears;

    @DecimalMin(value = "0.0", inclusive = false, message = "must be greater than 0")
    @DecimalMax(value = "99999999.99", inclusive = true, message = "must be at most 99999999.99")
    @Digits(integer = 9, fraction = 2, message = "must have at most 2 decimal places")
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", inclusive = false, message = "must be greater than 0")
    @DecimalMax(value = "99999999.99", inclusive = true, message = "must be at most 99999999.99")
    @Digits(integer = 9, fraction = 2, message = "must have at most 2 decimal places")
    private BigDecimal salaryMax;

    @Pattern(regexp = "^[A-Z]{3}$", message = "must be a exactly 3-letter currency code")
    private String currencyCode;

    @Future(message = "must be in the future")
    private LocalDateTime expiresAt;
}
