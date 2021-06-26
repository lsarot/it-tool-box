FROM amazoncorretto:11-alpine-jdk
MAINTAINER leo.com
COPY target/spring-config-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]