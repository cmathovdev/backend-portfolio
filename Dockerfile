# Etapa 1: build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=build /app/target/leaderboard-1.0.jar app.jar

CMD ["sh", "-c", "java -jar app.jar \
  --spring.datasource.url=$DB_URL \
  --spring.datasource.username=$DB_USER \
  --spring.datasource.password=$DB_PASS \
  --server.port=${PORT:-10000}"]