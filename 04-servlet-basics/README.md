# 04 · Servlet Basics (Jakarta Servlet 6)

Everything in Spring MVC runs on top of this. Learn the raw request lifecycle first.

| File | Concepts |
|---|---|
| `HelloServlet.java` | `@WebServlet`, `doGet`, query params, writing a response |
| `CounterServlet.java` | `HttpSession`, `JSESSIONID`, why servlets must be stateless (one instance, many threads) |
| `LoggingFilter.java` | `@WebFilter`, filter chain, timing a request (the same idea as Spring Security's filter chain) |

Run tests: `./mvnw -pl 04-servlet-basics test` (Mockito fakes the request/response).
Package: `./mvnw -pl 04-servlet-basics package` → deploy `target/*.war` to Tomcat 11+ / Jetty 12+.

## Request lifecycle
`client → container (Tomcat) → Filter chain → Servlet.service() → doGet/doPost → response`
Lifecycle methods: `init()` once → `service()` per request (on a pool thread) → `destroy()` on shutdown.

## Most asked interview questions
1. Servlet lifecycle (`init` / `service` / `destroy`) — who calls them?
2. Is a servlet thread-safe? (No — one instance shared by all request threads; avoid instance fields.)
3. `forward` vs `sendRedirect` — server-side, same request vs new client request (URL changes).
4. `GET` vs `POST`; idempotent and safe methods.
5. Session tracking: cookies, URL rewriting, `HttpSession`; session vs stateless JWT.
6. Filter vs Servlet vs Listener; where does Spring Security plug in? (`DelegatingFilterProxy`).
7. `javax.*` → `jakarta.*` migration — why did the namespace change? (Java EE moved to Eclipse Foundation.)
8. `ServletContext` vs `ServletConfig`.

## Best practices
- Keep servlets stateless; put state in the session/DB.
- Use filters for cross-cutting concerns (logging, auth, CORS, compression).
- Always set content type and character encoding.

## Common pitfalls
- Mutable instance fields in servlets (race conditions).
- Writing to the response after it has been committed.
- Mixing `javax.servlet` and `jakarta.servlet` jars.
