package com.footballtalks.footballtalks.repository;

import com.footballtalks.footballtalks.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("""
            select tr from Transfer tr
            join fetch tr.player p
            left join fetch tr.fromTeam
            left join fetch tr.toTeam
            order by tr.transferDate desc, tr.id desc
            """)
    List<Transfer> findAllWithRelations();

    @Query("""
            select tr from Transfer tr
            join fetch tr.player p
            left join fetch tr.fromTeam
            left join fetch tr.toTeam
            where p.id = :playerId
            order by tr.transferDate desc, tr.id desc
            """)
    List<Transfer> findByPlayerIdWithRelations(Long playerId);

    @Query("""
            select tr from Transfer tr
            where tr.player.id = :playerId
            and ((:fromTeamId is null and tr.fromTeam is null) or tr.fromTeam.id = :fromTeamId)
            and ((:toTeamId is null and tr.toTeam is null) or tr.toTeam.id = :toTeamId)
            and tr.transferDate = :transferDate
            """)
    Optional<Transfer> findExistingTransfer(@Param("playerId") Long playerId,
                                            @Param("fromTeamId") Long fromTeamId,
                                            @Param("toTeamId") Long toTeamId,
                                            @Param("transferDate") LocalDate transferDate);
}
