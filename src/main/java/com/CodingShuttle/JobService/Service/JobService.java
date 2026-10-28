package com.CodingShuttle.JobService.Service;

import com.CodingShuttle.JobService.Dto.JobCreateRequestDto;
import com.CodingShuttle.JobService.Dto.JobDto;
import com.CodingShuttle.JobService.Entity.Enum.JobStatus;
import com.CodingShuttle.JobService.Entity.Job;
import com.CodingShuttle.JobService.Exception.BadRequestException;
import com.CodingShuttle.JobService.Exception.JobAlreadyClosedException;
import com.CodingShuttle.JobService.Exception.ResourceNotFoundException;
import com.CodingShuttle.JobService.Repository.JobRepository;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public JobDto getJobById(Long jobId){
        return modelMapper.map(requireJob(jobId), JobDto.class);
    }

    Job requireJob(Long jobId){
        Optional<Job> job;
        try {
            job = jobRepository.findById(jobId);
        } catch (DataAccessException ex) {
            log.error("Failed to retrieve job with ID: {}", jobId, ex);
            throw ex;
        }
        return job.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
    }

    @Transactional
    public void closeJob(Long jobId, Long callerId){
        if (jobId == null || jobId <= 0) {
            throw new BadRequestException("Job ID must be greater than 0.");
        }

        Job job = requireJob(jobId);

        if(!Objects.equals(job.getRecruiterId(), callerId)){
            throw new ForbiddenException("You do not own this job posting.");
        }

        if(job.getJobStatus() == JobStatus.CLOSED){
            throw new JobAlreadyClosedException("Job with id " + jobId + " is already closed.");
        }

        job.setJobStatus(JobStatus.CLOSED);
        try {
            jobRepository.save(job);
        } catch (DataAccessException ex) {
            log.error("Failed to close job {} for recruiter {}", jobId, callerId, ex);
            throw ex;
        }
        log.info("Job {} closed by recruiter {}", jobId, callerId);
    }
}
