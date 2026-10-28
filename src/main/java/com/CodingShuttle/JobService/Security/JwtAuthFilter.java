package com.CodingShuttle.JobService.Security;

import com.CodingShuttle.JobService.Exception.ApiError;
import com.CodingShuttle.JobService.Exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter{
    public static final String USER_ID_ATTRIBUTE = "userId";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final String[] PUBLIC_GET_PATTERNS = {
            "/jobs/search",
            "/jobs/recruiters/**",
            "/jobs/*"
    };

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        //No creadentials provided, allow the public reads through, reject everything else.
        if(header == null || header.isBlank()){
            if(isPublic(request)) {
                filterChain.doFilter(request, response);
            } else {
                writeError(response, "Access token is missing.", HttpStatus.UNAUTHORIZED);
            }
            return;
        }
        //Credentials were offered,so they are validated even on public paths.
        //A caller sending a broken token should be told, not silently downgraded.
        if(!header.startsWith(BEARER_PREFIX)){
            writeError(response, "Authorization header must use the Bearer scheme.", HttpStatus.UNAUTHORIZED);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();

        try {
            Long userId = jwtService.getUserIdFromToken(token);
            request.setAttribute(USER_ID_ATTRIBUTE, userId);
        } catch (UnauthorizedException ex) {
            writeError(response, ex.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublic(HttpServletRequest request){
        //getRequestURI() includes the servelet context path (/jobs), but the patterns in
        //this class are relative to the application root, so strip the context path first.

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if(PATH_MATCHER.match("/actuator/**", path)){
            return true;
        }
        if(!"GET".equalsIgnoreCase(request.getMethod())){
            return false;
        }

        for(String pattern : PUBLIC_GET_PATTERNS){
            if(PATH_MATCHER.match(pattern, path)){
                return true;
            }
        }
        return false;
    }

    private void writeError(HttpServletResponse response, String message, HttpStatus status) throws IOException{
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new ApiError(message,status));
    }
}
