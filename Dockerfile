# Multi-stage build para otimizar o tamanho da imagem
FROM eclipse-temurin:21-jdk-alpine AS builder

# Instalar dependências necessárias
RUN apk add --no-cache curl

WORKDIR /app

# Copiar arquivos de configuração do Gradle
COPY gradle/ gradle/
COPY gradlew .
COPY gradle.properties .
COPY build.gradle .

# Garantir que o gradlew tenha permissão de execução
RUN chmod +x gradlew

# Copiar código fonte
COPY src/ src/

# Build da aplicação
RUN ./gradlew clean build -x test --no-daemon

# Estágio final - runtime
FROM eclipse-temurin:21-jre-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copiar JAR do build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Configurar ownership
RUN chown -R appuser:appgroup /app

# Usar usuário não-root
USER appuser

# Expor porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Configurar JVM para containers
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Ponto de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]