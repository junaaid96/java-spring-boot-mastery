# Multi-stage build: compile with the full JDK, run on a slim JRE.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
# The reactor POM lists every module, so copy the whole repo (.dockerignore keeps target/ and .git out).
COPY . .
RUN ./mvnw -B -q -pl 06-spring-boot -am package -DskipTests \
 && java -Djarmode=tools -jar 06-spring-boot/target/job-board.jar extract --layers --launcher --destination /extracted

FROM eclipse-temurin:25-jre
WORKDIR /app
# Run as an unprivileged user.
RUN useradd --system --uid 1001 spring
USER spring
# Layers change least to most often, which keeps image rebuilds small.
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "org.springframework.boot.loader.launch.JarLauncher"]
