package com.CodingShuttle.JobService.Service;

import com.CodingShuttle.JobService.Exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageRequestBuilder {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private static final Set<String> SORTABLE_JOB_FIELDS = Set.of("createdAt","salaryMin","salaryMax");

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

    //For parsing sort expression for job search

    public static Sort parseSort(String sort){
        String field = "createdAt";
        Sort.Direction direction = Sort.Direction.DESC;

        if(sort != null && !sort.isBlank()){
            String[] parts = sort.split(",");
            if(parts.length > 2){
                throw new BadRequestException("Parameter 'sort' must reference one of" + SORTABLE_JOB_FIELDS + ".");
            }
            if(parts.length == 2){
                String rawDirection = parts[1].trim();
                if(rawDirection.equalsIgnoreCase("asc")){
                    direction = Sort.Direction.ASC;
                } else if(rawDirection.equalsIgnoreCase("desc")){
                    direction = Sort.Direction.DESC;
                } else {
                    throw new BadRequestException("Sort direction must be 'asc' or 'desc'.");
                }
            }
        }
        Sort.Order primary = new Sort.Order(direction,field).nullsLast();
        return Sort.by(primary).and(Sort.by(Sort.Order.desc("id")));
    }
}
