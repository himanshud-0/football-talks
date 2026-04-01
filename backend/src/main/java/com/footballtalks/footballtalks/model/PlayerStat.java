package com.footballtalks.footballtalks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "player_stats",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_player_stats_row",
                columnNames = {"player_id", "team_id", "competition_id", "season"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Column(nullable = false, length = 10)
    private String season;

    private Integer appearances = 0;

    private Integer goals = 0;

    private Integer assists = 0;

    private Integer minutes = 0;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
