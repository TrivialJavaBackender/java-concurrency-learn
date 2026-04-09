# TODO: Реализуй двухэтапный Dockerfile
# Stage 1 (build): maven:3.9-eclipse-temurin-21 — компиляция
# Stage 2 (runtime): eclipse-temurin:21-jre-alpine — только jar
#
# Порядок в Stage 1:
# 1. COPY pom.xml
# 2. RUN mvn dependency:go-offline (кэшируется отдельно)
# 3. COPY src/
# 4. RUN mvn package -DskipTests
