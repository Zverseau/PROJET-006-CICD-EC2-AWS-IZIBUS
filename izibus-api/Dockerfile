#Generer le build
FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#Conteneriser
FROM eclipse-temurin:21-jre-ubi9-minimal
ARG PROFILE=dev
ARG APP_VERSION=1.0.0
WORKDIR /app
COPY --from=build /app/target/*.jar ./izibusapi.jar
EXPOSE 8080
ENV DB_URL=jdbc:mysql://mysql:3306/izibus
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}
CMD ["sh", "-c", "java -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} -jar izibusapi.jar"]