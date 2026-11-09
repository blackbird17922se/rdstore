# Etapa de compilación
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests


# Etapa de ejecución
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/rdstore-0.0.1-SNAPSHOT.jar app.jar

ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.7.9/applicationinsights-agent-3.7.9.jar applicationinsights-agent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/applicationinsights-agent.jar", "-jar", "app.jar"]