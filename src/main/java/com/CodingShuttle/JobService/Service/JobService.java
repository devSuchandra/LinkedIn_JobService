package com.CodingShuttle.JobService.Service;

import com.CodingShuttle.JobService.Dto.JobCreateRequestDto;
import com.CodingShuttle.JobService.Dto.JobDto;
import com.CodingShuttle.JobService.Entity.Enum.JobStatus;
import com.CodingShuttle.JobService.Entity.Job;
import com.CodingShuttle.JobService.Exception.BadRequestException;
import com.CodingShuttle.JobService.Repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public JobDto createJob(JobCreateRequestDto request, Long recruiterId) {
        if(request.getSalaryMin() != null && request.getSalaryMax() != null
           && request.getSalaryMin().compareTo(request.getSalaryMax()) > 0) {
            throw new BadRequestException("Minimum salary cannot be greater than maximum salary.");
        }
        Job job = modelMapper.map(request, Job.class);
        job.setId(null);
        job.setCompanyId(request.getCompanyId());
        job.setRecruiterId(recruiterId);
        job.setJobStatus(JobStatus.OPEN);

        Job savedJob;
        try {
            savedJob = jobRepository.save(job);
        } catch (DataAccessException ex) {
            log.error("Failed to create job for recruiter: {}", recruiterId, ex);
            throw ex;
        }
        log.info("Job created by recruiter: {} with ID: {}", recruiterId, savedJob.getId());
        return modelMapper.map(savedJob, JobDto.class);
    }

}
