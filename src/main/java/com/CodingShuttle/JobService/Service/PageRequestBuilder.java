package com.CodingShuttle.JobService.Service;

import com.CodingShuttle.JobService.Exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public final class PageRequestBuilder {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageRequestBuilder() {
        // Private constructor to prevent instantiation
    }

    public static PageRequest of(int page, int size, Sort sort){
        validate(page, size);
        return PageRequest.of(page, size, sort);
    }

    public static PageRequest of(int page, int size){
        validate(page, size);
        return PageRequest.of(page, size);
    }

    private static void validate(int page, int size){
        if(page < 0){
            throw new BadRequestException("Parameter 'page' must be greater than or equal to 0");
        }
        if(size < 1 || size > MAX_SIZE){
            throw new BadRequestException("Parameter 'size' must be between 1 and " + MAX_SIZE + ".");
        }
    }
}
