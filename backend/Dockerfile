# =========================================================
# Build stage
# =========================================================
# The image builds the application itself, so the only thing needed on the
# host is Docker. The JDK is not carried into the final image.
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# The wrapper and the build scripts change far less often than the source,
# so they are copied first and the dependencies are resolved in a layer of
# their own. Editing a Java file then rebuilds only the last step instead
# of downloading every dependency again.
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

# =========================================================
# Runtime stage
# =========================================================
# Only a Java runtime and the jar survive into the image that actually
# ships, which keeps it a fraction of the size of the build stage.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# The application only reads from disk, so it runs as an unprivileged user.
# A container that is never root cannot be talked into acting as one.
RUN addgroup -S reverie && adduser -S reverie -G reverie

COPY --from=build --chown=reverie:reverie /app/build/libs/*.jar app.jar

USER reverie

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]