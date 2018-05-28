FROM openjdk:8-alpine
MAINTAINER Sysco Middleware
RUN mkdir amqp-helper
WORKDIR /amqp-helper/
ADD target/amqp-helper-0.0.2-SNAPSHOT.jar .
CMD java -jar amqp-helper-0.0.2-SNAPSHOT.jar