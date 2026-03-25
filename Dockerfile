FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .

# En Windows, `gradlew` puede venir con saltos de línea CRLF y/o sin permisos de ejecución.
# Normalizamos y aseguramos permisos antes de compilar.
RUN chmod +x ./gradlew && sed -i 's/\r$//' ./gradlew && ./gradlew clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

