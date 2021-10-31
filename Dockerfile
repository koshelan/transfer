FROM adoptopenjdk/openjdk11:x86_64-ubuntu-jdk-11.0.12_7-slim
EXPOSE 5500
ADD target/transfer-0.0.1-SNAPSHOT.jar run.jar
ENTRYPOINT ["java", "-jar", "/run.jar"]