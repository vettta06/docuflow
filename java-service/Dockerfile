FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY java-service/pom.xml .
COPY java-service/src ./src
COPY java-service/proto ./proto
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/java-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
