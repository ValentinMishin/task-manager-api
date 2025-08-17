FROM eclipse-temurin:11-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
COPY build/libs/task-manager-api-*.jar app.jar
CMD ["java", "-jar", "app.jar"]