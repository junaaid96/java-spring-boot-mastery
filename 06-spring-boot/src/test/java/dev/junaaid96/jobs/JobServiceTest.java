package dev.junaaid96.jobs;

import dev.junaaid96.jobs.config.JobBoardProperties;
import dev.junaaid96.jobs.domain.JobPost;
import dev.junaaid96.jobs.domain.JobPostRepository;
import dev.junaaid96.jobs.service.JobNotFoundException;
import dev.junaaid96.jobs.service.JobService;
import dev.junaaid96.jobs.web.JobDtos.JobRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Base of the testing pyramid: fast unit tests with mocked collaborators, no Spring context.
class JobServiceTest {

    private final JobPostRepository repository = mock(JobPostRepository.class);
    private final JobService service = new JobService(repository,
            new JobBoardProperties("x".repeat(32), 60, 100));

    @Test
    void createMapsRequestToEntityAndBack() {
        // Repository echoes back whatever it is asked to save.
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var response = service.create(new JobRequest("Java Dev", "Build APIs", 2, List.of("Java", "Spring")));
        assertThat(response.title()).isEqualTo("Java Dev");
        assertThat(response.skills()).containsExactly("Java", "Spring");
        verify(repository).save(any(JobPost.class));
    }

    @Test
    void getUnknownIdThrows() {
        when(repository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(42)).isInstanceOf(JobNotFoundException.class).hasMessageContaining("42");
    }
}
