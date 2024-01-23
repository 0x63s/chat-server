FROM maven:3.9.6-eclipse-temurin-21 as build
WORKDIR /usr/src/app
COPY src ./src
COPY pom.xml .
RUN mvn package

FROM openjdk:23-oracle
COPY --from=build /usr/src/app/target/*.jar ./app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 50001