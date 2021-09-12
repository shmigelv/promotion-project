FROM adoptopenjdk/openjdk11:ubi

COPY target/promotion-project-0.0.1-SNAPSHOT.jar promotion-project-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/promotion-project-0.0.1-SNAPSHOT.jar"]