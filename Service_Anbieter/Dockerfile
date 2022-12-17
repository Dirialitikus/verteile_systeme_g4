FROM maven:3.8.5-jdk-11 AS BUILD
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/

RUN mvn package


FROM openjdk:11


COPY --from=BUILD /tmp/target/Service_Anbieter-1.0-SNAPSHOT-jar-with-dependencies.jar /app/Service_Anbieter.jar
WORKDIR /app

CMD ["java", "-jar", "/app/Service_Anbieter.jar"]