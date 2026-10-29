package dev.junaaid96.jobs.service;

// Domain exception; translated to HTTP 404 ProblemDetail by the global handler.
public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(long id) {
        super("Job " + id + " not found");
    }
}
