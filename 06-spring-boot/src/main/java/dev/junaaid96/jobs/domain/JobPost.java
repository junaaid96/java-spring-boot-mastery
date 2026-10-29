package dev.junaaid96.jobs.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// JPA entity = a table row. Entities stay inside the service layer; the API exposes DTO records instead.
@Entity
@Table(name = "job_posts")
public class JobPost {

    // Database-generated primary key (IDENTITY works well with PostgreSQL).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Column constraints mirror the validation rules on the DTO.
    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    private int experienceYears;

    // A simple value collection stored in its own table (job_post_skills).
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_post_skills", joinColumns = @JoinColumn(name = "job_post_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    // Audit timestamp set automatically before insert.
    private Instant createdAt;

    // Optimistic locking: concurrent updates fail instead of silently overwriting each other.
    @Version
    private long version;

    // JPA requires a no-args constructor (protected keeps it out of normal use).
    protected JobPost() {}

    public JobPost(String title, String description, int experienceYears, List<String> skills) {
        update(title, description, experienceYears, skills);
    }

    // Lifecycle callback.
    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    // Behaviour on the entity instead of public setters everywhere.
    public void update(String title, String description, int experienceYears, List<String> skills) {
        this.title = title;
        this.description = description;
        this.experienceYears = experienceYears;
        this.skills = new ArrayList<>(skills);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getExperienceYears() { return experienceYears; }
    public List<String> getSkills() { return List.copyOf(skills); }
    public Instant getCreatedAt() { return createdAt; }
}
