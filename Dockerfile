# syntax=docker/dockerfile:1.4

# Etapa de build: compila o projeto usando o Gradle Wrapper
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace/app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle ./
COPY build.gradle ./
COPY src ./src

RUN chmod +x gradlew \
    && ./gradlew clean bootJar -x test --no-daemon \
    && find build/libs -name "*-plain.jar" -delete \
    && mv build/libs/*.jar app.jar

# Etapa final: imagem leve apenas com o runtime necessário
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

COPY --from=builder /workspace/app/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
