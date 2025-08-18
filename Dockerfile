FROM eclipse-temurin:11-jdk
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test
RUN cp build/libs/task-manager-api-*.jar app.jar
CMD ["java", "-jar", "app.jar"]