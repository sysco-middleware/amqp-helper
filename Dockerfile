FROM openjdk:8-alpine
MAINTAINER nikita.zhevnitskiy@sysco.no
RUN mkdir amqp-helper
WORKDIR /amqp-helper/
ADD target/amqp-helper-0.0.3-SNAPSHOT.jar .
CMD java -jar amqp-helper-0.0.3-SNAPSHOT.jar