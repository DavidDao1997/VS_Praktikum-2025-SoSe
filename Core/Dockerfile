# Use Maven and JDK to build
FROM docker.io/maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app
COPY pom.xml .
COPY Core/pom.xml ./Core/pom.xml
COPY Core/src ./Core/src
COPY Middleware/pom.xml ./Middleware/pom.xml
COPY Middleware/src ./Middleware/src
COPY ActuatorController/pom.xml ./ActuatorController/pom.xml
COPY ActuatorController/src ./ActuatorController/src
COPY View/pom.xml ./View/pom.xml
COPY View/src ./View/src
RUN mvn package -DskipTests

# Use JRE image to run
FROM docker.io/eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/Core/target/Core-1.0-SNAPSHOT.jar app.jar

EXPOSE 45054
CMD ["java", "-jar", "app.jar"]
