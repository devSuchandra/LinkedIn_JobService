 -----build stage -------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

COPY .mvn/  .mvn/
COPY mvnw pom.xml  ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

#----runtime stage-------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /build/target/*.jar app.jar
EXPOSE 9040
ENTRYPOINT ["java","-XX:MaxRAMPercentage=70.0","-jar","/app.jar"]
