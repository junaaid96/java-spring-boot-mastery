package dev.junaaid96.jobs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Top of the pyramid: the whole app against a real PostgreSQL in Docker.
// Skipped automatically on machines without Docker; always runs in CI (GitHub runners have Docker).
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
class JobApiIntegrationTest {

    // @ServiceConnection wires spring.datasource.* to this container automatically.
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired MockMvc mvc;

    @Test
    void loginCreateReadDelete() throws Exception {
        // 1. Log in with the demo admin user and grab the JWT.
        String tokenJson = mvc.perform(post("/api/v1/auth/token").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + JsonPath.read(tokenJson, "$.accessToken");

        // 2. Create a job with the token.
        String created = mvc.perform(post("/api/v1/jobs").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Spring Engineer\",\"description\":\"APIs\",\"experienceYears\":3,\"skills\":[\"Java\"]}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        // 3. Read it back publicly, search for it, then delete it.
        mvc.perform(get("/api/v1/jobs/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.skills[0]").value("Java"));
        mvc.perform(get("/api/v1/jobs?q=spring")).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(delete("/api/v1/jobs/" + id).header("Authorization", bearer)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/jobs/" + id)).andExpect(status().isNotFound());

        // 4. Actuator health is public.
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
