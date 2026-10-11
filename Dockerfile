# ==============================================================================
# Multi-stage Dockerfile para Sistema de Oficina Mecânica (Java 21 / Spring Boot 3)
# ==============================================================================

# ------------------------------------------------------------------------------
# 1º Estágio: Build e Empacotamento com Maven
# ------------------------------------------------------------------------------
FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /build

# Otimização de cache: copia apenas o pom.xml primeiro
COPY pom.xml .

# Download prévio das dependências em modo batch
RUN mvn dependency:go-offline -B || true

# Copia a árvore de código-fonte
COPY src ./src

# Compilação e empacotamento da aplicação gerando o JAR executável
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# 2º Estágio: Imagem Final Otimizada para Execução (Alpine JRE)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criação de grupo e usuário não-root dedicado para execução segura
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Cópia do artefato final a partir do estágio de build
COPY --from=builder /build/target/*.jar app.jar

# Ajuste de propriedade do diretório e arquivo para o appuser
RUN chown -R appuser:appgroup /app

# Transição para o usuário não-root
USER appuser

# Exposição da porta padrão da API
EXPOSE 8080

# Configurações de JVM otimizadas para ambiente de contêineres
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Comando de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

