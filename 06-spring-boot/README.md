# 06 · Spring Boot 4 Job Board API (Java 25)

A production-style REST API that pulls everything together. It is the modern version of the old `JobApp`, `SpringBootREST`, `SpringDataJPA`, `SpringSec` and `SpringAOP` demos.

## Stack
Spring Boot 4.1 · Spring Framework 7 · Java 25 · Spring Data JPA + PostgreSQL · Spring Security OAuth2 Resource Server (JWT) · Bean Validation · `ProblemDetail` (RFC 9457) · springdoc-openapi 3 · Actuator + Micrometer/Prometheus · Spring Cache · virtual threads · JUnit 5 + Mockito + `@WebMvcTest` + Testcontainers 2.

## Layout
```
dev.junaaid96.jobs
├── JobBoardApplication        @SpringBootApplication + @ConfigurationPropertiesScan
├── config/   JobBoardProperties (type-safe, validated @ConfigurationProperties record), CacheConfig, OpenApiConfig
├── domain/   JobPost (@Entity, @Version optimistic locking), JobPostRepository
├── service/  JobService (@Transactional, @Cacheable/@CacheEvict), JobNotFoundException
├── security/ SecurityConfig (stateless JWT, scopes), TokenController
└── web/      JobController (/api/v1/jobs), JobDtos (records), GlobalExceptionHandler (ProblemDetail)
```

## Run it
```bash
# 1. start PostgreSQL
docker compose -f 06-spring-boot/compose.yaml up -d db
# 2. run the app (from the repo root)
./mvnw -pl 06-spring-boot spring-boot:run
# or everything in containers
docker compose -f 06-spring-boot/compose.yaml up --build
```
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health · Metrics: `/actuator/prometheus`

### Try the API
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/token -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}' | jq -r .accessToken)
curl -X POST localhost:8080/api/v1/jobs -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"Java Developer","description":"Build APIs","experienceYears":2,"skills":["Java","Spring"]}'
curl 'localhost:8080/api/v1/jobs?q=java&page=0&size=10'
```
Demo users (in-memory, for learning only): `admin/admin123` (scope `jobs:write`), `viewer/viewer123` (read only).
`GET` is public; `POST/PUT/DELETE` require `SCOPE_jobs:write`.

## Test pyramid
| Level | Test | What it proves |
|---|---|---|
| Unit | `JobServiceTest` | business logic with a mocked repository, no Spring |
| Slice | `JobControllerTest` | `@WebMvcTest` + security + validation + ProblemDetail, service mocked with `@MockitoBean` |
| Integration | `JobApiIntegrationTest` | real PostgreSQL via Testcontainers + `@ServiceConnection`, full login → create → read → delete flow. Skipped automatically when Docker is not available. |

`./mvnw -pl 06-spring-boot verify`

## Configuration & profiles
- `application.yml`: defaults for local development. Every secret comes from an env var (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `JOBBOARD_JWT_SECRET`).
- `prod` profile (`SPRING_PROFILES_ACTIVE=prod`): `ddl-auto=validate`, API docs off.
- `JobBoardProperties` is validated at startup, so a short JWT secret fails fast.

## Most asked interview questions
1. What does `@SpringBootApplication` combine? (`@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`)
2. How does auto-configuration work? (`AutoConfiguration.imports` + `@ConditionalOn…`). How do you debug it? (`--debug`, `/actuator/conditions`)
3. Starters, the parent POM / BOM and dependency management.
4. `application.yml` vs profiles vs env vars: precedence order. `@Value` vs `@ConfigurationProperties`.
5. `@Transactional`: propagation, isolation, rollback rules (unchecked only by default), self-invocation pitfall, read-only.
6. JPA: N+1 problem and fixes (`JOIN FETCH`, `@EntityGraph`, DTO projections), lazy vs eager, `open-in-view`.
7. `save` vs `saveAndFlush`; entity states (transient, managed, detached, removed); optimistic vs pessimistic locking.
8. Global exception handling: `@RestControllerAdvice` + `ProblemDetail`.
9. Spring Security filter chain; authentication vs authorization; stateless JWT vs sessions; CSRF and when it is safe to disable it.
10. How do you validate input? (`@Valid`, constraint annotations, custom validators, groups)
11. Actuator endpoints and securing them; liveness vs readiness probes.
12. Virtual threads in Boot (`spring.threads.virtual.enabled`): when they help.
13. Caching: `@Cacheable` / `@CacheEvict`, cache keys, why caching entities is risky.
14. Testing: `@SpringBootTest` vs slice tests (`@WebMvcTest`, `@DataJpaTest`), `@MockitoBean` (replaces deprecated `@MockBean`), Testcontainers.
15. What changed in Boot 4 / Spring 7? Jakarta EE 11, Jackson 3 (`tools.jackson`), modular starters (`spring-boot-starter-webmvc`), JSpecify null-safety, API versioning, `RestClient`/HTTP interface clients.

## Best practices used here
- DTO records at the API boundary; entities never leave the service layer.
- Constructor injection, thin controllers, `@Transactional` on the service.
- API versioned in the path (`/api/v1`), `201 Created` + `Location` header, paginated responses with a capped page size.
- Errors as RFC 9457 `ProblemDetail` with field-level validation messages.
- Stateless security with least-privilege scopes, BCrypt passwords, secrets from the environment.
- `open-in-view: false`, optimistic locking with `@Version`.
- Only the needed Actuator endpoints exposed.
- Non-root, layered Docker image built from the JRE.

## Common pitfalls
- `ddl-auto=update` in production (use Flyway/Liquibase plus `validate`).
- Returning JPA entities from controllers (lazy-loading exceptions, leaking fields, infinite JSON recursion).
- `@Transactional` on private methods or self-calls.
- Hard-coded secrets in `application.yml`.
- Jackson 3 rejects a missing value for a primitive field by default (`FAIL_ON_NULL_FOR_PRIMITIVES`). Send it, or use a wrapper type.
- Annotations such as `@EnableCaching` on the main class also apply to test slices. Keep them in their own `@Configuration`.
