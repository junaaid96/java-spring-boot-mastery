package dev.junaaid96.jobs.service;

import dev.junaaid96.jobs.config.JobBoardProperties;
import dev.junaaid96.jobs.domain.JobPost;
import dev.junaaid96.jobs.domain.JobPostRepository;
import dev.junaaid96.jobs.web.JobDtos.JobRequest;
import dev.junaaid96.jobs.web.JobDtos.JobResponse;
import dev.junaaid96.jobs.web.JobDtos.PageResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service layer: business rules + transaction boundaries. Controllers stay thin.
@Service
// Read-only transactions by default (lets Hibernate skip dirty checking); writes override below.
@Transactional(readOnly = true)
public class JobService {

    private final JobPostRepository repository;
    private final JobBoardProperties properties;

    // Constructor injection: Spring passes the beans in; no @Autowired needed with one constructor.
    public JobService(JobPostRepository repository, JobBoardProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    // Paged search; page size is clamped so clients cannot request huge pages.
    public PageResponse<JobResponse> search(String keyword, int page, int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), properties.maxPageSize()),
                Sort.by("id").descending());
        Page<JobPost> result = repository.findByTitleContainingIgnoreCase(keyword == null ? "" : keyword, pageable);
        return new PageResponse<>(result.map(JobService::toResponse).getContent(), result.getNumber(),
                result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    // Cached by id: the second call for the same id skips the database.
    @Cacheable(cacheNames = "jobs", key = "#id")
    public JobResponse get(long id) {
        return repository.findById(id).map(JobService::toResponse).orElseThrow(() -> new JobNotFoundException(id));
    }

    // Write transaction.
    @Transactional
    public JobResponse create(JobRequest request) {
        var saved = repository.save(new JobPost(request.title(), request.description(),
                request.experienceYears(), request.skills()));
        return toResponse(saved);
    }

    // Update evicts the stale cache entry. Changes flush automatically at commit (dirty checking).
    @Transactional
    @CacheEvict(cacheNames = "jobs", key = "#id")
    public JobResponse update(long id, JobRequest request) {
        var job = repository.findById(id).orElseThrow(() -> new JobNotFoundException(id));
        job.update(request.title(), request.description(), request.experienceYears(), request.skills());
        return toResponse(job);
    }

    @Transactional
    @CacheEvict(cacheNames = "jobs", key = "#id")
    public void delete(long id) {
        if (!repository.existsById(id)) throw new JobNotFoundException(id);
        repository.deleteById(id);
    }

    // Entity -> DTO mapping in one place (MapStruct can generate this in bigger projects).
    static JobResponse toResponse(JobPost job) {
        return new JobResponse(job.getId(), job.getTitle(), job.getDescription(), job.getExperienceYears(),
                job.getSkills(), job.getCreatedAt());
    }
}
