package dev.junaaid96.mvc;

import dev.junaaid96.mvc.aop.TimingAspect;
import dev.junaaid96.mvc.config.AppConfig;
import dev.junaaid96.mvc.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Loads the real Spring config into a test web context and drives it with MockMvc (no server needed).
@SpringJUnitWebConfig({AppConfig.class, WebConfig.class})
class ProgrammerControllerTest {

    @Autowired WebApplicationContext context;
    @Autowired TimingAspect aspect;
    MockMvc mvc;

    @BeforeEach
    void setUp() { mvc = MockMvcBuilders.webAppContextSetup(context).build(); }

    @Test
    void createThenFetch() throws Exception {
        mvc.perform(post("/programmers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Ana\",\"language\":\"Java\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/programmers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ana")));
        mvc.perform(get("/programmers/99")).andExpect(status().isNotFound());
        // The aspect wrapped the service calls.
        assertThat(aspect.calls()).isGreaterThanOrEqualTo(3);
    }
}
