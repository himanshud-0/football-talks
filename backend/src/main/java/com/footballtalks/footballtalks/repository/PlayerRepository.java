package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByExternalApiId(Long externalApiId);
    Optional<Player> findByNameIgnoreCase(String name);

    @Query("""
            select p from Player p
            left join fetch p.team t
            left join fetch t.competition
            """)
    List<Player> findAllWithRelations();

    @Query("""
            select p from Player p
            left join fetch p.team t
            left join fetch t.competition
            where p.id = :id
            """)
    Optional<Player> findByIdWithRelations(Long id);
}
