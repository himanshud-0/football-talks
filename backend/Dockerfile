FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY . .

# build inside backend folder
RUN cd backend && mvn clean package -DskipTests

# 👇 IMPORTANT: run from correct path
CMD ["java", "-jar", "/app/backend/target/football-talks-0.0.1-SNAPSHOT.jar"]