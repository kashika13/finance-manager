# Use official OpenJDK image as base
FROM openjdk:24-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/finance-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose port (make sure it matches your Spring Boot server.port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
