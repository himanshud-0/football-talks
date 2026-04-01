package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.PlayerStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {

    @Query("""
            select ps from PlayerStat ps
            left join fetch ps.team t
            left join fetch ps.competition c
            where ps.player.id = :playerId
            order by ps.season desc, ps.id desc
            """)
    List<PlayerStat> findByPlayerIdWithRelations(Long playerId);

    Optional<PlayerStat> findByPlayerIdAndTeamIdAndCompetitionIdAndSeason(
            Long playerId,
            Long teamId,
            Long competitionId,
            String season
    );
}
