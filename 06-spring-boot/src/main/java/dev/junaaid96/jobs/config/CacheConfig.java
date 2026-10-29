package dev.junaaid96.jobs.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// Enables @Cacheable / @CacheEvict. Kept in its own class (not on the main class) so test slices like @WebMvcTest don't need a CacheManager.
@Configuration
@EnableCaching
public class CacheConfig {}
