package com.footballtalks.footballtalks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private Long id;
    private String name;
    private String firstname;
    private String lastname;
    private Integer age;
    private String nationality;
    private String position;
    private Long marketValue;
    private TeamSummaryResponse teamSummary;
    private CompetitionSummaryResponse competition;
    private String team;
    private String club;
    private String league;
    private String image;
    private String photo;
    private String description;
    private Integer appearances;
    private Integer goalsScored;
    private Integer assists;
    private Integer minutesPlayed;
    private Integer yellowCards;
    private Integer redCards;
    private BigDecimal rating;
    private String specialAbility;
}
