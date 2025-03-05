# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the application JAR from the Gradle build
COPY build/libs/DemoProject-1.0-SNAPSHOT.jar DemoProject-1.0-SNAPSHOT.jar

# Set environment variables with defaults
ENV KAFKA_BROKER=localhost:19092
ENV KAFKA_TOPIC=test-topic
ENV MESSAGE_INTERVAL=1000

# Run the application
CMD ["sh", "-c", "java -jar DemoProject-1.0-SNAPSHOT.jar"]