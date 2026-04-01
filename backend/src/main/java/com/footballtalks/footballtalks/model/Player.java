package com.footballtalks.footballtalks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "external_api_id", unique = true)
    private Long externalApiId;

    @Column(length = 120)
    private String firstname;

    @Column(length = 120)
    private String lastname;

    private Integer age;

    @Column(length = 60)
    private String nationality;

    @Column(length = 40)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private Long marketValue = 0L;

    private Integer appearances = 0;

    @Column(name = "goals_scored")
    private Integer goalsScored = 0;

    private Integer assists = 0;

    @Column(length = 120)
    private String specialAbility = "";

    private Integer minutesPlayed = 0;

    private Integer yellowCards = 0;

    private Integer redCards = 0;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
