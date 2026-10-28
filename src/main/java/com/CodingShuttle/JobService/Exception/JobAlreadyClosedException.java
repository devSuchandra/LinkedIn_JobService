package com.CodingShuttle.JobService.Exception;

public class JobAlreadyClosedException extends RuntimeException {
    public JobAlreadyClosedException(String message) {
        super(message);
    }
}
