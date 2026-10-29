package dev.junaaid96.mvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

// Java-based configuration (replaces the old spring.xml). @ComponentScan finds @Service/@Component/@Aspect beans.
@Configuration
@ComponentScan(basePackages = {"dev.junaaid96.mvc.service", "dev.junaaid96.mvc.aop"})
// Turns on proxy creation so @Aspect advice runs around bean methods.
@EnableAspectJAutoProxy
public class AppConfig {}
