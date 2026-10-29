package dev.junaaid96.jobs.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import dev.junaaid96.jobs.config.JobBoardProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

// Replaces the old hand-written JwtFilter: Spring Security's resource server validates Bearer tokens for us.
@Configuration
public class SecurityConfig {

    // The filter chain: who can call what.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Stateless JWT API: no cookies, so CSRF protection is not needed.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: docs, health, token endpoint and read-only job endpoints.
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/jobs/**").permitAll()
                        // Writes need the "jobs:write" scope inside the JWT.
                        .requestMatchers("/api/v1/jobs/**").hasAuthority("SCOPE_jobs:write")
                        // Everything else (e.g. other actuator endpoints) needs any valid token.
                        .anyRequest().authenticated())
                // Validate "Authorization: Bearer <jwt>" with the JwtDecoder bean below.
                .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));
        return http.build();
    }

    // One HMAC key shared by encoder and decoder (in production prefer an external IdP with RSA keys/JWKS).
    private static SecretKey key(JobBoardProperties props) {
        return new SecretKeySpec(props.jwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    JwtDecoder jwtDecoder(JobBoardProperties props) {
        return NimbusJwtDecoder.withSecretKey(key(props)).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtEncoder jwtEncoder(JobBoardProperties props) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key(props)));
    }

    // Delegating encoder stores "{bcrypt}..." so the algorithm can be upgraded later.
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Demo users kept in memory; swap for a JPA-backed UserDetailsService in a real app.
    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("admin123")).authorities("jobs:write").build(),
                User.withUsername("viewer").password(encoder.encode("viewer123")).authorities("jobs:read").build());
    }

    // Checks username/password for the token endpoint.
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService users, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }
}
