# ===== Stage 1: build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Зависимости кэшируются отдельно от кода
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src/ src/
RUN mvn package -q -DskipTests

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -q -O- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
