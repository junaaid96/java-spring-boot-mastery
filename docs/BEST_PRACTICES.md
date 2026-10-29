# Java & Spring Boot Best Practices (2026)

A checklist distilled from all modules. Each item links to the code that shows it.

## Java
- **Immutability first**: records, `final` fields, `List.of`/`Map.of`. → `01-java-core/RecordsAndSealed`
- **Model closed hierarchies with `sealed`** and handle them with exhaustive `switch` pattern matching.
- **`Optional` only as a return type**; never for fields, parameters or collections.
- **Virtual threads for blocking I/O**: `Executors.newVirtualThreadPerTaskExecutor()`. Don't pool them; limit concurrency with a `Semaphore`. → `01-java-core/ConcurrencyBasics`
- **`ScopedValue` over `ThreadLocal`** for request-scoped context.
- **try-with-resources** for anything `AutoCloseable`. Keep the cause when you wrap exceptions.
- **Choose collections by access pattern** (see the Big-O table in `02-collections`). Default to `ArrayList`, `HashMap`, `ArrayDeque`.
- Always implement `equals` and `hashCode` together, and keep map keys immutable.

## Design
- **SOLID + composition over inheritance.** → `03-oop-and-solid`
- **Constructor injection** with `final` fields. Depend on interfaces at boundaries.
- Apply patterns when the code asks for them, not up front (YAGNI, KISS, DRY).

## Spring Boot APIs
- **Layering**: controller (HTTP + validation) → service (transactions, business rules) → repository.
- **DTO records at the edge**. Never serialise JPA entities.
- **Validate input** with Bean Validation (`@Valid`, `@NotBlank`, `@Size`…).
- **Errors as `ProblemDetail`** (RFC 9457) from one `@RestControllerAdvice`.
- **Version the API** (`/api/v1`). Return `201 + Location` on create and `204` on delete. Paginate with a capped page size.
- **Type-safe config**: `@ConfigurationProperties` records with `@Validated`. Secrets come from env vars or a secret manager.
- **Profiles** for environment differences only (`prod` = `ddl-auto=validate`, docs off).

## Data
- `spring.jpa.open-in-view=false`.
- Use schema migrations (Flyway/Liquibase) in real projects, with `ddl-auto=validate`.
- Watch for N+1 queries: `JOIN FETCH`, `@EntityGraph`, projections.
- Use optimistic locking (`@Version`) for concurrent updates.
- `@Transactional(readOnly = true)` for reads. Remember that self-invocation bypasses the proxy.

## Security
- Stateless JWT resource server, least-privilege scopes, BCrypt or Argon2 password hashing.
- Disable CSRF only for stateless token APIs.
- Expose only the Actuator endpoints you need, and secure them in production.
- Never commit secrets. Rotate signing keys. Prefer asymmetric keys (RS256/ES256) or an identity provider (Keycloak, Auth0, Okta…) in production.

## Observability & operations
- Actuator health with liveness and readiness probes. Micrometer metrics with a Prometheus registry. Tracing via Micrometer Observation / OpenTelemetry.
- Structured logging (`logging.structured.format.console=ecs`). Never log secrets or personal data.
- Container images: multi-stage builds, JRE base, non-root user, layered jars, `MaxRAMPercentage`.
- Graceful shutdown is on by default in Boot. Keep requests idempotent where you can.

## Testing
- **Test pyramid**: many unit tests, some slice tests (`@WebMvcTest`, `@DataJpaTest`), a few `@SpringBootTest` + Testcontainers.
- Use `@MockitoBean` (`@MockBean` is deprecated) and AssertJ for fluent assertions.
- Test against the real database engine with Testcontainers + `@ServiceConnection`, not H2.
- Run the full build in CI on every PR (`.github/workflows/ci.yml`).

## What's new to know (interview hot topics)
- **Java 21–25**: virtual threads, record patterns, pattern-matching `switch`, sequenced collections, `ScopedValue` (final in 25), unnamed variables `_`, compact source files and instance `main`, flexible constructor bodies, module import declarations. Structured concurrency is still in preview.
- **Spring Boot 4 / Spring 7**: Jakarta EE 11, Jackson 3 (`tools.jackson`), modular auto-configuration and starters, JSpecify null-safety, built-in API versioning, HTTP interface clients, `RestClient` (over `RestTemplate`), resilience annotations (`@Retryable`, `@ConcurrencyLimit`).
