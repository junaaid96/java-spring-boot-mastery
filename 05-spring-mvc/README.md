# 05 · Spring MVC (Spring Framework 7, no Boot)

See what Spring Boot auto-configures for you by wiring it by hand once.

| File | Concepts |
|---|---|
| `config/WebAppInitializer.java` | replaces `web.xml`; registers the `DispatcherServlet` (Servlet 3+ `ServletContainerInitializer`) |
| `config/AppConfig.java` | root context, `@ComponentScan`, `@EnableAspectJAutoProxy` |
| `config/WebConfig.java` | `@EnableWebMvc` web context |
| `service/ProgrammerService.java` | `@Service`, constructor injection, thread-safe in-memory store |
| `web/ProgrammerController.java` | `@RestController`, `@GetMapping`/`@PostMapping`, `@PathVariable`, `ResponseEntity` |
| `aop/TimingAspect.java` | `@Aspect`, `@Around`, pointcut expressions |

Tests use `@SpringJUnitWebConfig` + `MockMvc`: `./mvnw -pl 05-spring-mvc test`.

## How a request flows
`DispatcherServlet → HandlerMapping → HandlerAdapter → @Controller → HttpMessageConverter (Jackson 3) → response`

## XML → Java config → Boot
The legacy repo had XML and Java-config demos. Today: **Java config** for plain Spring, **auto-configuration** with Boot. XML is only for maintaining old apps.

## Most asked interview questions
1. What is IoC / DI? Constructor vs setter vs field injection (prefer constructor).
2. Bean scopes: singleton, prototype, request, session. Singleton ≠ GoF singleton (one per container).
3. Bean lifecycle: instantiate → populate → `@PostConstruct` → use → `@PreDestroy`.
4. `@Component` vs `@Service` vs `@Repository` vs `@Controller`; `@Controller` vs `@RestController`.
5. `@Bean` vs `@Component`; `@Configuration` proxying (`proxyBeanMethods`).
6. How does `DispatcherServlet` find the handler?
7. AOP terms: aspect, advice, join point, pointcut, weaving. JDK dynamic proxy vs CGLIB.
8. Why does `@Transactional` / an aspect not work on a self-invocation? (calls bypass the proxy).
9. `@RequestParam` vs `@PathVariable` vs `@RequestBody`.
10. Circular dependencies — how does Spring react? (forbidden by default since Boot 2.6; fix the design).

## Best practices
- Constructor injection with `final` fields, no `@Autowired` needed for one constructor.
- Keep controllers thin: validate → call service → map to DTO.
- Return `ResponseEntity` / proper status codes (201 + `Location` on create).

## Common pitfalls
- Field injection (hard to test, hides dependencies).
- Aspects on `private` methods or self-calls (never proxied).
- Two contexts scanning the same beans (root and web context overlap).
