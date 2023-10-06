FROM openjdk:17-alpine

ARG JAR_FILE_PATH=build/libs/MemberServer-0.0.1-SNAPSHOT.jar

COPY $JAR_FILE_PATH MemberServer-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "MemberServer-0.0.1-SNAPSHOT.jar"]