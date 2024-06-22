# Use the Maven base image
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app

# Copy only the POM file to leverage Docker cache
COPY pom.xml /app/pom.xml

# Download dependencies as a separate layer to improve build cache
RUN mvn dependency:go-offline -B

# Copy the source code after resolving dependencies to avoid re-downloading them unless the POM changes
COPY src /app/src

# Package the application without running tests for faster builds
RUN mvn -f /app/pom.xml clean package -DskipTests

# Use a smaller JRE base image for the final image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy only the packaged JAR from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Expose the port on which your application runs
EXPOSE 8181

# Define the Docker container's entrypoint
ENTRYPOINT ["java", "-jar", "/app/app.jar"]