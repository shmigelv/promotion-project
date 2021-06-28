FROM adoptopenjdk/openjdk11:ubi

COPY target/promotionProject-0.0.1-SNAPSHOT.jar promotionProject-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","/promotionProject-0.0.1-SNAPSHOT.jar"]