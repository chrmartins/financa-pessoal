# Dockerfile para produção - Railway
FROM openjdk:21-jdk-slim

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos do projeto
COPY . .

# Dar permissão de execução ao gradlew
RUN chmod +x ./gradlew

# Build da aplicação (pulando testes para deploy rápido)
RUN ./gradlew clean build -x test --no-daemon

# Comando de start com ordem correta dos parâmetros
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -Dspring.profiles.active=prod -jar build/libs/*.jar"]

# Configurar variáveis de ambiente padrão
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV PORT=8080