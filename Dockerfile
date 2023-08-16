FROM openjdk:18-jdk-alpine3.13

EXPOSE 5050

ADD target/netology-cloud-storage-0.0.1-SNAPSHOT.jar diploma.jar

ENTRYPOINT ["java", "-jar", "diploma.jar"]