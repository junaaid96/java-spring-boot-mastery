package dev.junaaid96.jobs.web;

import dev.junaaid96.jobs.service.JobService;
import dev.junaaid96.jobs.web.JobDtos.JobRequest;
import dev.junaaid96.jobs.web.JobDtos.JobResponse;
import dev.junaaid96.jobs.web.JobDtos.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// REST controller: maps HTTP to service calls, nothing more. Versioned base path.
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService service;

    public JobController(JobService service) { this.service = service; }

    // GET /api/v1/jobs?q=java&page=0&size=20  (public)
    @GetMapping
    @Operation(summary = "Search job posts")
    public PageResponse<JobResponse> search(@RequestParam(required = false) String q,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return service.search(q, page, size);
    }

    // GET /api/v1/jobs/{id}  (public)
    @GetMapping("/{id}")
    public JobResponse get(@PathVariable long id) { return service.get(id); }

    // POST -> 201 Created with a Location header pointing to the new resource.
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest request) {
        var created = service.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // PUT replaces the job's fields.
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public JobResponse update(@PathVariable long id, @Valid @RequestBody JobRequest request) {
        return service.update(id, request);
    }

    // DELETE -> 204 No Content.
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
