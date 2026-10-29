# Java & Spring Boot Learning Path

[![CI](https://github.com/junaaid96/exploring-servlet-spring-mvc-spring-boot/actions/workflows/ci.yml/badge.svg)](https://github.com/junaaid96/exploring-servlet-spring-mvc-spring-boot/actions/workflows/ci.yml)
![Java 25](https://img.shields.io/badge/Java-25-orange)
![Spring Boot 4.1](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

One repo to go from core Java to a production-style Spring Boot 4 API. Every source file has line-by-line learning comments. Every module has a README with concepts, the **most asked interview questions**, best practices and common pitfalls.

## Study order
| # | Module | You will learn |
|---|---|---|
| 1 | [01-java-core](01-java-core) | records, sealed types, pattern matching, streams, `Optional`, generics, exceptions, virtual threads, `ScopedValue` |
| 2 | [02-collections](02-collections) | List/Set/Map/Queue, `HashMap` internals, Big-O, comparators, LRU cache |
| 3 | [03-oop-and-solid](03-oop-and-solid) | OOP pillars, SOLID (violation vs fix), GoF patterns used by Spring |
| 4 | [04-servlet-basics](04-servlet-basics) | Jakarta Servlet lifecycle, sessions, filters |
| 5 | [05-spring-mvc](05-spring-mvc) | IoC/DI, Java config, `DispatcherServlet`, REST controllers, AOP (no Boot) |
| 6 | [06-spring-boot](06-spring-boot) | Boot 4 REST API: validation, ProblemDetail, JPA + PostgreSQL, JWT security, OpenAPI, Actuator, caching, profiles, virtual threads, Testcontainers, Docker |

Cross-cutting guide: [docs/BEST_PRACTICES.md](docs/BEST_PRACTICES.md).

## Versions
Java 25 (LTS) · Spring Boot 4.1.2 · Spring Framework 7 · Jakarta EE 11 · Jackson 3 · Hibernate 7 · springdoc-openapi 3.2 · Testcontainers 2 · PostgreSQL 18 · Maven 3.9 (wrapper included)

## Quick start
```bash
# Requires JDK 25. Docker is optional (integration tests are skipped without it).
./mvnw verify                          # build and test every module
./mvnw -pl 02-collections test         # one module
./mvnw -pl 06-spring-boot spring-boot:run   # needs PostgreSQL, see 06-spring-boot/README.md
```

## Repository layout
```
├── 01-java-core/ … 06-spring-boot/   one Maven module per topic
├── docs/BEST_PRACTICES.md
├── .github/workflows/ci.yml          JDK 25 build + tests + Docker image build
└── pom.xml                           reactor POM (Spring Boot parent manages versions)
```

## History
This repo merges the earlier `exploring_java` and `java_collection_framework` repositories plus the original Servlet / Spring MVC / Spring Boot demos (XML config, Java config, JDBC, Spring Data JPA/REST, Security, AOP, JobApp), rewritten for Java 25 and Spring Boot 4.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md). Licensed under [MIT](LICENSE).
