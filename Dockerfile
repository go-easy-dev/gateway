FROM eclipse-temurin:17-alpine
WORKDIR /home/app
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar /home/app/libs/gateway-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "-Duser.timezone=Europe/Moscow","/home/app/libs/gateway-0.0.1-SNAPSHOT.jar"]
