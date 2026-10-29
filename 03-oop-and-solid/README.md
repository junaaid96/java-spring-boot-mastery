# 03 · OOP, SOLID & Design Patterns

| File | Concepts |
|---|---|
| `concepts/OopConcepts.java` | encapsulation, abstraction, inheritance vs composition, polymorphism (overloading/overriding) |
| `solid/Solid.java` | each SOLID principle with a *violation* (in comments) and the *fix* (code) |
| `patterns/Patterns.java` | Singleton (enum), Factory, Builder, Strategy, Observer, Decorator, Adapter |

Run: `./mvnw -pl 03-oop-and-solid test`

## SOLID in one line each
- **S**ingle Responsibility — one reason to change per class.
- **O**pen/Closed — add behaviour by adding new types, not editing old `if/else` chains.
- **L**iskov Substitution — subtypes must honour the parent's contract (the classic Square/Rectangle trap).
- **I**nterface Segregation — many small, client-specific interfaces over one fat one.
- **D**ependency Inversion — depend on abstractions; inject implementations (this is what Spring's DI does for you).

## Where Spring uses these patterns
| Pattern | Spring example |
|---|---|
| Singleton | default bean scope |
| Factory | `BeanFactory`, `@Bean` methods |
| Proxy / Decorator | AOP, `@Transactional`, `@Cacheable` |
| Template Method | `JdbcTemplate`, `RestClient` |
| Strategy | `PasswordEncoder`, `HandlerMapping` |
| Observer | `ApplicationEvent` / `@EventListener` |
| Adapter | `HandlerAdapter` |

## Most asked interview questions
1. Four pillars of OOP, with a real example each.
2. Abstract class vs interface (default/static/private methods since Java 8/9).
3. Composition over inheritance — why? (fragile base class, tighter coupling).
4. Overloading vs overriding; can you override static/private methods? (no).
5. Explain each SOLID principle and give a violation you have fixed.
6. How does DI relate to DIP? Constructor vs field injection?
7. Thread-safe Singleton options (enum, holder idiom, double-checked locking with `volatile`).
8. Builder vs telescoping constructors; Strategy vs `switch`.
9. Which design patterns does Spring use internally?

## Best practices
- Favour small immutable objects and constructor injection.
- Use sealed hierarchies when the set of subtypes is fixed.
- Don't apply patterns speculatively (YAGNI) — refactor to them when duplication appears.

## Common pitfalls
- God classes / "Manager" classes that break SRP.
- Inheritance purely for code reuse.
- Interfaces with a single implementation "just because" (fine at boundaries, noise elsewhere).
