package com.footballtalks.footballtalks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transfers",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_player_transfer_event",
                columnNames = {"player_id", "from_team_id", "to_team_id", "transfer_date"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_team_id")
    private Team fromTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_team_id", nullable = false)
    private Team toTeam;

    private Long transferFee;

    @Column(length = 40)
    private String transferType;

    private LocalDate transferDate;

    @Column(length = 10)
    private String season;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
