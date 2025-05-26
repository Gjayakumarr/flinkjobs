FROM openjdk:17-jdk-alpine
WORKDIR /docker
#ENV PORT 8989
EXPOSE 8989
COPY target/flinkjobs.jar /docker/flinkjobs.jar
ENTRYPOINT [ "java" , "-jar" , "user-microservice.jar" ]