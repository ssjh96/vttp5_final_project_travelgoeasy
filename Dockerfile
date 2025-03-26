# build the client
FROM  node:23 AS buildang

WORKDIR /src

# Copy Angular source
COPY client/*.json .
COPY client/public public
COPY client/src src

# Run npm to install node_modules -> package.json
RUN npm ci
RUN npm i -g @angular/cli
# produce dist/client/browser
RUN ng build

# Build the SpringBoot application
FROM maven:3.9.9-eclipse-temurin-23 AS buildjava

WORKDIR /src

COPY server/mvnw .
COPY server/pom.xml .
COPY server/src src
COPY server/.mvn .mvn

# copy the angular application over to static directory
COPY --from=buildang /src/dist/client/browser/* src/main/resources/static
COPY --from=buildang /src/dist/client/browser/icons src/main/resources/static/icons
COPY --from=buildang /src/dist/client/browser/images src/main/resources/static/images

# make mvnw executable
RUN chmod a+x mvnw
# produce target/server-0.0.1-SNAPSHOT.jar
# RUN ./mvnw package -Dtest.skip=true
RUN ./mvnw package -Dmaven.test.skip=true

# Deployment container
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=buildjava /src/target/server-0.0.1-SNAPSHOT.jar app.jar

# set environment variable
ENV PORT=3000

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar