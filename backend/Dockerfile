FROM maven:3.9.6-eclipse-temurin-17

WORKDIR /app

COPY backend ./backend

WORKDIR /app/backend

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]