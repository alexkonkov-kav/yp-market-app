
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src ./src
RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java", "-jar", "target/yp-market-app-0.0.1-SNAPSHOT.jar"]