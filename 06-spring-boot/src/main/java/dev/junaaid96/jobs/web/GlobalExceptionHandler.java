package dev.junaaid96.jobs.web;

import dev.junaaid96.jobs.service.JobNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

// Central error handling. Every error is an RFC 9457 ProblemDetail (application/problem+json).
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Domain "not found" -> 404.
    @ExceptionHandler(JobNotFoundException.class)
    public ProblemDetail handleNotFound(JobNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Job not found");
        problem.setType(URI.create("https://example.com/problems/job-not-found"));
        return problem;
    }

    // Validation errors -> 400 with a field -> message map so clients can show inline errors.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(fe -> fe.getField(), fe -> String.valueOf(fe.getDefaultMessage()), (a, b) -> a));
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
