package com.footballtalks.footballtalks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballTalksApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballTalksApplication.class, args);
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║                                                              ║\n" +
                "║        🏟️  FOOTBALL TALKS BACKEND IS NOW RUNNING  ⚽         ║\n" +
                "║                                                              ║\n" +
                "║  🌐 API:     http://localhost:8080                          ║\n" +
                "║  📚 Swagger: http://localhost:8080/swagger-ui/index.html    ║\n" +
                "║  ❤️  Health:  http://localhost:8080/api/health              ║\n" +
                "║                                                              ║\n" +
                "╚══════════════════════════════════════════════════════════════╝\n");
    }
}