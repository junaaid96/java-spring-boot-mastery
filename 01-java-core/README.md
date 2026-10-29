# 01 · Java Core (Java 25)

Modern Java you will use every day: records, sealed types, pattern matching, streams, `Optional`, generics, exceptions and concurrency with virtual threads.

| File | Concepts |
|---|---|
| `RecordsAndSealed.java` | records, compact constructors, sealed interfaces, `switch` pattern matching, record patterns |
| `StreamsAndOptional.java` | stream pipeline, collectors (`groupingBy`, `partitioningBy`, `toMap`), `Optional` done right |
| `GenericsAndExceptions.java` | bounded generics, PECS (`? extends` / `? super`), checked vs unchecked, try-with-resources |
| `ConcurrencyBasics.java` | virtual threads, `ExecutorService` per task, `ScopedValue` (final in 25) |

Run: `./mvnw -pl 01-java-core test`

## Most asked interview questions
1. **`==` vs `equals()`** — reference identity vs logical equality. Override `equals` *and* `hashCode` together (records do it for you).
2. **Why is `String` immutable?** — security, string-pool sharing, thread-safety, cached hash.
3. **Record vs class vs Lombok `@Data`?** — records are immutable, transparent data carriers; no setters, no inheritance.
4. **What does `sealed` give you?** — a closed hierarchy, so `switch` can be exhaustive without `default`.
5. **Checked vs unchecked exceptions?** — checked = recoverable and must be declared; unchecked = programming errors. Modern APIs favour unchecked.
6. **Intermediate vs terminal stream ops / laziness?** — nothing runs until a terminal op; streams are single-use.
7. **`map` vs `flatMap`**, **`findFirst` vs `findAny`**, **when not to use parallel streams**.
8. **`Optional` misuse?** — don't use it for fields/params, never call `get()` blindly; use `orElseThrow`, `map`, `ifPresentOrElse`.
9. **Platform vs virtual threads?** — virtual threads are cheap, JVM-scheduled; great for blocking I/O, no benefit for CPU-bound work; don't pool them.
10. **`ThreadLocal` vs `ScopedValue`?** — `ScopedValue` is immutable, bounded to a scope, cheap with millions of virtual threads.
11. **Type erasure** and why `new T()` / `List<int>` are impossible. **PECS**: Producer Extends, Consumer Super.
12. **`volatile` vs `synchronized` vs `Atomic*`** — visibility vs mutual exclusion vs lock-free CAS.

## Best practices
- Prefer immutability: records, `List.of`, `final` fields.
- Model alternatives with sealed interfaces + pattern matching instead of `instanceof` chains.
- Use `var` only when the type is obvious from the right-hand side.
- Always close resources with try-with-resources.
- For blocking I/O-heavy concurrency use `Executors.newVirtualThreadPerTaskExecutor()`.

## Common pitfalls
- Mutating shared state inside streams/lambdas.
- Swallowing exceptions (`catch (Exception e) {}`) or losing the cause when re-throwing.
- `synchronized` blocks around long I/O on virtual threads (fixed pinning in JDK 24+, but still keep critical sections short).
- Comparing boxed `Integer`s with `==` (works only in the -128..127 cache).
