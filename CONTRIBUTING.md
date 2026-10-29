# Contributing

Thanks for helping improve this learning path!

## Setup
- JDK 25 (e.g. Temurin). Use the included Maven wrapper (`./mvnw`).
- Docker is optional. You need it to run the Testcontainers integration test and the compose stack.

## Workflow
1. Create a branch: `feat/<topic>` or `fix/<topic>`.
2. Keep each module self-contained and add or update tests with your change.
3. Run `./mvnw verify` locally. CI must pass before merge.
4. Use [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `chore:`).
5. Open a pull request with a short description of what learners gain.

## Style guide for learning code
- Comment the *why*, not just the *what*. Every non-trivial line should teach something.
- Prefer modern idioms (records, `var` where obvious, pattern matching, constructor injection).
- Show a "bad" example in comments next to the "good" code when it helps.
- Update the module README (concepts, interview questions, pitfalls) when you add a topic.
- No secrets, personal data or machine-specific paths in code or docs.
