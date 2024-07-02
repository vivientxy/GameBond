# Build Angular project:
FROM node:20.15.0 AS ng-builder

WORKDIR /frontend

RUN npm i -g @angular/cli@17.3.8

COPY frontend/angular.json .
COPY frontend/package*.json .
COPY frontend/tsconfig*.json .
COPY frontend/src src

RUN npm ci && ng build

# Build SpringBoot project:
FROM maven:3-eclipse-temurin-21 AS sb-builder

WORKDIR /backend

COPY backend/mvnw .
COPY backend/mvnw.cmd .
COPY backend/pom.xml .
COPY backend/.mvn .mvn
COPY backend/src src

COPY --from=ng-builder /frontend/dist/frontend/browser/ src/main/resources/static

RUN chmod a+x mvnw 
RUN mvn package -Dmaven.test.skip=true

# Build Java .jar file
FROM openjdk:21

WORKDIR /app 

COPY --from=sb-builder /backend/target/project-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

# Environment variables:
ENV TELEGRAM_TOKEN=
ENV MYSQL_URL=
ENV MYSQL_USERNAME=
ENV MYSQL_PASSWORD=
ENV REDISHOST=
ENV REDISPORT=
ENV REDISUSER=
ENV REDISPASSWORD=
ENV S3_ACCESS_KEY=
ENV S3_SECRET_KEY=
ENV S3_BUCKET=
ENV MAIL_USERNAME=
ENV MAIL_PASSWORD=
ENV STRIPE_SECRET_KEY=

# Run the program
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar