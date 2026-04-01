package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByExternalApiId(Long externalApiId);
    Optional<Team> findByNameIgnoreCase(String name);

    @Query("""
            select t from Team t
            left join fetch t.competition
            order by t.name asc
            """)
    List<Team> findAllWithCompetition();

    @Query("""
            select t from Team t
            left join fetch t.competition
            where t.id = :id
            """)
    Optional<Team> findByIdWithCompetition(Long id);
}
