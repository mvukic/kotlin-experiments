FROM eclipse-temurin:22-jre-alpine
COPY build/libs/kotlin-experiments-1.0-SNAPSHOT.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-Dspring.profiles.active=prod","-jar","/app.jar"]
