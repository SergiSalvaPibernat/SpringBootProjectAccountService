FROM openjdk:25-ea-21-jdk-slim-bullseye


# Make sure the software on the container is up to date
RUN apt-get update && apt-get -y upgrade
RUN apt-get -y install netcat
RUN mkdir /app

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container at /app/books.jar
COPY build/libs/AccountService-0.0.1-SNAPSHOT.jar accountservice.jar

# Define which port number can be mapped to this container
EXPOSE 8081

# Define defaults for O/S environment variables
# These can be overridden at runtime with -e switch to docker run command
# or in docker-compose.yml environment section


# Start the container as a non-root user for security


# How to run the application
ENTRYPOINT [ "java", "-jar", "accountservice.jar" ]
# CMD [ "--spring.profiles.active=dev" ]