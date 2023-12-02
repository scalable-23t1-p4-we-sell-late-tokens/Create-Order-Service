# Build Stage
FROM maven:3.8.4 AS build
WORKDIR /app
COPY create-order/pom.xml .
COPY create-order/src ./src
RUN mvn clean package

# Final Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar ./main.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .
ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 -Dotel.metrics.exporter=otlp"
CMD ["java", "-jar", "main.jar"]
