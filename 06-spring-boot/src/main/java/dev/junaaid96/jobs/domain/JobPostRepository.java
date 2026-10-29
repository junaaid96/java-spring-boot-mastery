package dev.junaaid96.jobs.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data generates the implementation at runtime: CRUD, paging and sorting for free.
public interface JobPostRepository extends JpaRepository<JobPost, Long> {

    // Derived query: SQL is generated from the method name (WHERE lower(title) LIKE lower(%?%)).
    Page<JobPost> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
