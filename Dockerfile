FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN apk add --no-cache tzdata
ENV TZ=Asia/Novosibirsk

ENTRYPOINT ["java", "-Duser.timezone=Asia/Novosibirsk", "-jar", "app.jar"]