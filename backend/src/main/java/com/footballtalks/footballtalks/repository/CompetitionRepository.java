package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findAllByOrderByNameAsc();
    Optional<Competition> findByExternalApiId(Long externalApiId);
    Optional<Competition> findByNameIgnoreCase(String name);
}
