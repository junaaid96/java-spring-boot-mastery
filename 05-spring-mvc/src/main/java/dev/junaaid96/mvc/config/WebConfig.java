package dev.junaaid96.mvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @EnableWebMvc registers handler mappings, message converters (JSON), validation, etc.
@Configuration
@EnableWebMvc
@ComponentScan("dev.junaaid96.mvc.web")
public class WebConfig implements WebMvcConfigurer {}
