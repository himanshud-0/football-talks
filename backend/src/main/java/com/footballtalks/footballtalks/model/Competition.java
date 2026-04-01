package com.footballtalks.footballtalks.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "competitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "external_api_id", unique = true)
    private Long externalApiId;

    @Column(length = 60)
    private String country;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
