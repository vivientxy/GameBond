# Build Angular project:
FROM node:21 AS ng-builder

RUN npm i -g @angular/cli

WORKDIR /ngapp

COPY frontend/angular.json .
COPY frontend/package*.json .
COPY frontend/tsconfig.* .
COPY frontend/src src

RUN npm ci && ng build

# Build SpringBoot project:
FROM maven:3-eclipse-temurin-21 AS sb-builder

WORKDIR /sbapp

COPY giphy/mvnw .
COPY giphy/mvnw.cmd .
COPY giphy/pom.xml .
COPY giphy/.mvn .mvn
COPY giphy/src src

COPY --from=ng-builder /ngapp/dist/frontend/browser/ /src/main/resources/static

RUN mvn package -Dmaven.test.skip=true

# Build Java .jar file
FROM openjdk:21-jdk-bullseye

WORKDIR /app 

COPY --from=sb-builder /sbapp/target/project-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

# Environment variables:
ENV TELEGRAM_TOKEN=

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar