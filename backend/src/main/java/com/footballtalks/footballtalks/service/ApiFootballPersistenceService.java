package com.footballtalks.footballtalks.service;

import com.footballtalks.footballtalks.dto.CompetitionSummaryResponse;
import com.footballtalks.footballtalks.dto.PlayerResponse;
import com.footballtalks.footballtalks.dto.TeamSummaryResponse;
import com.footballtalks.footballtalks.dto.TransferResponse;
import com.footballtalks.footballtalks.model.Competition;
import com.footballtalks.footballtalks.model.Player;
import com.footballtalks.footballtalks.model.PlayerStat;
import com.footballtalks.footballtalks.model.Team;
import com.footballtalks.footballtalks.model.Transfer;
import com.footballtalks.footballtalks.repository.CompetitionRepository;
import com.footballtalks.footballtalks.repository.PlayerRepository;
import com.footballtalks.footballtalks.repository.PlayerStatRepository;
import com.footballtalks.footballtalks.repository.TeamRepository;
import com.footballtalks.footballtalks.repository.TransferRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ApiFootballPersistenceService {

    private final CompetitionRepository competitionRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;
    private final TransferRepository transferRepository;

    public ApiFootballPersistenceService(CompetitionRepository competitionRepository,
                                         TeamRepository teamRepository,
                                         PlayerRepository playerRepository,
                                         PlayerStatRepository playerStatRepository,
                                         TransferRepository transferRepository) {
        this.competitionRepository = competitionRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.playerStatRepository = playerStatRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public int persistPlayers(List<PlayerResponse> players, String season) {
        int persistedCount = 0;
        for (PlayerResponse playerResponse : players) {
            Competition competition = upsertCompetition(playerResponse.getCompetition());
            Team team = upsertTeam(playerResponse.getTeamSummary(), competition);
            Player player = upsertPlayer(playerResponse, team);
            upsertPlayerStat(player, team, competition, season, playerResponse);
            persistedCount++;
        }
        return persistedCount;
    }

    public Competition upsertCompetition(@Nullable CompetitionSummaryResponse source) {
        if (source == null || source.getId() == null) {
            return null;
        }

        Competition competition = competitionRepository.findByExternalApiId(source.getId())
                .or(() -> source.getName() != null ? competitionRepository.findByNameIgnoreCase(source.getName()) : java.util.Optional.empty())
                .orElseGet(Competition::new);
        competition.setExternalApiId(source.getId());
        competition.setName(source.getName());
        competition.setCountry(source.getCountry());
        competition.setLogoUrl(source.getLogoUrl());
        return competitionRepository.save(competition);
    }

    public Team upsertTeam(@Nullable TeamSummaryResponse source,
                           @Nullable Competition competition) {
        if (source == null || source.getId() == null) {
            return null;
        }

        Team team = teamRepository.findByExternalApiId(source.getId())
                .or(() -> source.getName() != null ? teamRepository.findByNameIgnoreCase(source.getName()) : java.util.Optional.empty())
                .orElseGet(Team::new);
        team.setExternalApiId(source.getId());
        team.setName(source.getName());
        team.setCountry(source.getCountry() != null ? source.getCountry() : competition != null ? competition.getCountry() : null);
        team.setLeague(source.getLeague() != null ? source.getLeague() : competition != null ? competition.getName() : null);
        team.setLogoUrl(source.getLogoUrl());
        team.setCompetition(competition);
        return teamRepository.save(team);
    }

    public Player upsertPlayer(PlayerResponse source,
                               @Nullable Team team) {
        if (source.getId() == null) {
            throw new RuntimeException("API player is missing id and cannot be persisted");
        }

        Player player = playerRepository.findByExternalApiId(source.getId())
                .or(() -> source.getName() != null ? playerRepository.findByNameIgnoreCase(source.getName()) : java.util.Optional.empty())
                .orElseGet(Player::new);
        player.setExternalApiId(source.getId());
        player.setName(source.getName());
        player.setFirstname(source.getFirstname());
        player.setLastname(source.getLastname());
        player.setAge(source.getAge());
        player.setNationality(source.getNationality());
        player.setPosition(source.getPosition());
        player.setTeam(team);
        player.setPhotoUrl(source.getPhoto());
        player.setDescription(source.getDescription());
        player.setAppearances(zeroIfNull(source.getAppearances()));
        player.setGoalsScored(zeroIfNull(source.getGoalsScored()));
        player.setAssists(zeroIfNull(source.getAssists()));
        player.setMinutesPlayed(zeroIfNull(source.getMinutesPlayed()));
        player.setYellowCards(zeroIfNull(source.getYellowCards()));
        player.setRedCards(zeroIfNull(source.getRedCards()));
        player.setRating(source.getRating() != null ? source.getRating() : BigDecimal.ZERO);
        player.setSpecialAbility(source.getSpecialAbility() != null ? source.getSpecialAbility() : "");

        Long incomingMarketValue = source.getMarketValue();
        if (incomingMarketValue != null && incomingMarketValue > 0) {
            player.setMarketValue(incomingMarketValue);
        } else if (player.getMarketValue() == null) {
            player.setMarketValue(0L);
        }

        return playerRepository.save(player);
    }

    private void upsertPlayerStat(Player player,
                                  @Nullable Team team,
                                  @Nullable Competition competition,
                                  String season,
                                  PlayerResponse source) {
        if (team == null || competition == null) {
            return;
        }

        PlayerStat playerStat = playerStatRepository
                .findByPlayerIdAndTeamIdAndCompetitionIdAndSeason(player.getId(), team.getId(), competition.getId(), season)
                .orElseGet(PlayerStat::new);

        playerStat.setPlayer(player);
        playerStat.setTeam(team);
        playerStat.setCompetition(competition);
        playerStat.setSeason(season);
        playerStat.setAppearances(zeroIfNull(source.getAppearances()));
        playerStat.setGoals(zeroIfNull(source.getGoalsScored()));
        playerStat.setAssists(zeroIfNull(source.getAssists()));
        playerStat.setMinutes(zeroIfNull(source.getMinutesPlayed()));
        playerStat.setRating(source.getRating() != null ? source.getRating() : BigDecimal.ZERO);

        playerStatRepository.save(playerStat);
    }

    @Transactional
    public int persistTransfers(List<TransferResponse> transfers) {
        int persistedCount = 0;
        for (TransferResponse transferResponse : transfers) {
            if (transferResponse.getPlayerId() == null || transferResponse.getToTeam() == null) {
                continue;
            }

            Team fromTeam = upsertTeam(transferResponse.getFromTeam(), null);
            Team toTeam = upsertTeam(transferResponse.getToTeam(), null);

            PlayerResponse playerSource = new PlayerResponse();
            playerSource.setId(transferResponse.getPlayerId());
            playerSource.setName(transferResponse.getPlayerName());
            playerSource.setPhoto(transferResponse.getPlayerImage());
            playerSource.setImage(transferResponse.getPlayerImage());
            playerSource.setMarketValue(transferResponse.getPlayerMarketValue());
            playerSource.setTeamSummary(transferResponse.getToTeam());
            playerSource.setClub(transferResponse.getToTeam() != null ? transferResponse.getToTeam().getName() : null);
            playerSource.setTeam(transferResponse.getToTeam() != null ? transferResponse.getToTeam().getName() : null);
            playerSource.setLeague(transferResponse.getToTeam() != null ? transferResponse.getToTeam().getLeague() : null);
            playerSource.setDescription(
                    transferResponse.getPlayerName() != null
                            ? transferResponse.getPlayerName() + " transfer data synced from API-Football."
                            : null
            );

            Player player = upsertPlayer(playerSource, toTeam);

            Transfer transfer = transferRepository.findExistingTransfer(
                    player.getId(),
                    fromTeam != null ? fromTeam.getId() : null,
                    toTeam != null ? toTeam.getId() : null,
                    transferResponse.getTransferDate()
            ).orElseGet(Transfer::new);

            transfer.setPlayer(player);
            transfer.setFromTeam(fromTeam);
            transfer.setToTeam(toTeam);
            transfer.setTransferFee(transferResponse.getTransferFee());
            transfer.setTransferType(transferResponse.getTransferType());
            transfer.setTransferDate(transferResponse.getTransferDate());
            transfer.setSeason(transferResponse.getSeason());

            transferRepository.save(transfer);
            persistedCount++;
        }
        return persistedCount;
    }

    private Integer zeroIfNull(@Nullable Integer value) {
        return value != null ? value : 0;
    }
}
