package dev.junaaid96.jobs;

import dev.junaaid96.jobs.config.JobBoardProperties;
import dev.junaaid96.jobs.security.SecurityConfig;
import dev.junaaid96.jobs.service.JobNotFoundException;
import dev.junaaid96.jobs.service.JobService;
import dev.junaaid96.jobs.web.JobController;
import dev.junaaid96.jobs.web.JobDtos.JobResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Middle of the pyramid: a web "slice" test. Only MVC + security load; the service is a mock.
@WebMvcTest(JobController.class)
@Import({SecurityConfig.class, JobControllerTest.Props.class})
class JobControllerTest {

    // Binds jobboard.* (from application.yml) so SecurityConfig can build its JWT beans.
    @TestConfiguration
    @EnableConfigurationProperties(JobBoardProperties.class)
    static class Props {}

    @Autowired MockMvc mvc;
    @MockitoBean JobService service;

    private static final String VALID = """
            {"title":"Java Dev","description":"Build APIs","experienceYears":2,"skills":["Java"]}""";

    @Test
    void getIsPublic() throws Exception {
        when(service.get(1)).thenReturn(new JobResponse(1L, "Java Dev", "d", 2, List.of("Java"), Instant.now()));
        mvc.perform(get("/api/v1/jobs/1")).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Java Dev"));
    }

    @Test
    void notFoundReturnsProblemDetail() throws Exception {
        when(service.get(9)).thenThrow(new JobNotFoundException(9));
        mvc.perform(get("/api/v1/jobs/9"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Job not found"));
    }

    @Test
    void createRequiresToken() throws Exception {
        mvc.perform(post("/api/v1/jobs").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithWrongScopeIsForbidden() throws Exception {
        mvc.perform(post("/api/v1/jobs").with(jwt().authorities(() -> "SCOPE_jobs:read"))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isForbidden());
    }

    @Test
    void createWithScopeReturns201() throws Exception {
        when(service.create(any())).thenReturn(new JobResponse(5L, "Java Dev", "Build APIs", 2, List.of("Java"), Instant.now()));
        mvc.perform(post("/api/v1/jobs").with(jwt().authorities(() -> "SCOPE_jobs:write"))
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/v1/jobs/5")));
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/v1/jobs").with(jwt().authorities(() -> "SCOPE_jobs:write"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\",\"description\":\"\",\"experienceYears\":0,\"skills\":[]}")) // Jackson 3 fails on missing primitives by default
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }
}
