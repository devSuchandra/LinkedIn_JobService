package com.CodingShuttle.JobService.Controller;

import com.CodingShuttle.JobService.Dto.JobCreateRequestDto;
import com.CodingShuttle.JobService.Dto.JobDto;
import com.CodingShuttle.JobService.Security.JwtAuthFilter;
import com.CodingShuttle.JobService.Service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobsController {

    private final JobService jobService;

    @PostMapping("/create-job")
    public ResponseEntity<JobDto> createJob(
            @Valid @RequestBody JobCreateRequestDto request,
            @RequestAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE) Long recruiterId){

        JobDto created = jobService.createJob(request, recruiterId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDto> getJob(@PathVariable Long jobId){
        return ResponseEntity.ok(jobService.getJobById(jobId));
    }

    @PatchMapping("/{jobId}/close")
    public ResponseEntity<Void> closeJob(
            @PathVariable Long jobId,
            @RequestAttribute(JwtAuthFilter.USER_ID_ATTRIBUTE) Long callerId){

        jobService.closeJob(jobId, callerId);
        return ResponseEntity.noContent().build();
    }

    
}
