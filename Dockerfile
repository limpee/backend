FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/limpee-1.0.0.jar limpee-1.0.0.jar
EXPOSE 80
CMD ["java", "-jar", "limpee-1.0.0.jar"]