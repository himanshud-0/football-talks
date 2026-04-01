package com.footballtalks.footballtalks.service;

import com.footballtalks.footballtalks.dto.*;
import com.footballtalks.footballtalks.model.*;
import com.footballtalks.footballtalks.repository.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransferMarketService {

    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;
    private final TransferRepository transferRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;

    public TransferMarketService(PlayerRepository playerRepository,
                                 PlayerStatRepository playerStatRepository,
                                 TransferRepository transferRepository,
                                 TeamRepository teamRepository,
                                 CompetitionRepository competitionRepository) {
        this.playerRepository = playerRepository;
        this.playerStatRepository = playerStatRepository;
        this.transferRepository = transferRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
    }

    public List<PlayerResponse> getPlayers(@Nullable String search,
                                           @Nullable String position,
                                           @Nullable Long teamId,
                                           @Nullable Long competitionId,
                                           @Nullable String sortBy,
                                           @Nullable String direction,
                                           @Nullable Integer limit) {

        Comparator<Player> comparator = buildPlayerComparator(sortBy, direction);
        int resolvedLimit = limit == null || limit < 1 ? Integer.MAX_VALUE : limit;

        return playerRepository.findAllWithRelations()
                .stream()
                .filter(player -> matchesPlayer(player, search, position, teamId, competitionId))
                .sorted(comparator)
                .limit(resolvedLimit)
                .map(this::toPlayerResponse)
                .toList();
    }

    public PlayerDetailResponse getPlayer(@NonNull Long playerId) {
        Player player = playerRepository.findByIdWithRelations(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));

        PlayerDetailResponse response = new PlayerDetailResponse();
        copyPlayerFields(toPlayerResponse(player), response);
        response.setStatsHistory(
                playerStatRepository.findByPlayerIdWithRelations(playerId)
                        .stream()
                        .map(this::toPlayerSeasonStatResponse)
                        .toList()
        );
        response.setTransferHistory(
                transferRepository.findByPlayerIdWithRelations(playerId)
                        .stream()
                        .map(this::toTransferResponse)
                        .toList()
        );
        return response;
    }

    public List<TransferResponse> getTransfers(@Nullable String search,
                                               @Nullable String season,
                                               @Nullable Integer limit) {
        int resolvedLimit = limit == null || limit < 1 ? Integer.MAX_VALUE : limit;

        return transferRepository.findAllWithRelations()
                .stream()
                .filter(transfer -> matchesTransfer(transfer, search, season))
                .limit(resolvedLimit)
                .map(this::toTransferResponse)
                .toList();
    }

    public List<TeamResponse> getTeams(@Nullable String search,
                                       @Nullable Long competitionId,
                                       @Nullable Integer limit) {
        int resolvedLimit = limit == null || limit < 1 ? Integer.MAX_VALUE : limit;

        List<Player> players = playerRepository.findAllWithRelations();
        Map<Long, List<Player>> playersByTeamId = players.stream()
                .filter(player -> player.getTeam() != null && player.getTeam().getId() != null)
                .collect(Collectors.groupingBy(player -> player.getTeam().getId()));

        return teamRepository.findAllWithCompetition()
                .stream()
                .filter(team -> matchesTeam(team, search, competitionId))
                .sorted(Comparator.comparing(Team::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(resolvedLimit)
                .map(team -> toTeamResponse(team, playersByTeamId.getOrDefault(team.getId(), List.of())))
                .toList();
    }

    public List<CompetitionResponse> getCompetitions() {
        List<Team> teams = teamRepository.findAllWithCompetition();
        List<Player> players = playerRepository.findAllWithRelations();

        Map<Long, Long> teamCounts = teams.stream()
                .filter(team -> team.getCompetition() != null && team.getCompetition().getId() != null)
                .collect(Collectors.groupingBy(team -> team.getCompetition().getId(), Collectors.counting()));

        Map<Long, Long> playerCounts = players.stream()
                .filter(player -> player.getTeam() != null
                        && player.getTeam().getCompetition() != null
                        && player.getTeam().getCompetition().getId() != null)
                .collect(Collectors.groupingBy(
                        player -> player.getTeam().getCompetition().getId(),
                        Collectors.counting()
                ));

        return competitionRepository.findAllByOrderByNameAsc()
                .stream()
                .map(competition -> new CompetitionResponse(
                        competition.getId(),
                        competition.getName(),
                        competition.getCountry(),
                        competition.getLogoUrl(),
                        teamCounts.getOrDefault(competition.getId(), 0L),
                        playerCounts.getOrDefault(competition.getId(), 0L)
                ))
                .toList();
    }

    private boolean matchesPlayer(Player player,
                                  @Nullable String search,
                                  @Nullable String position,
                                  @Nullable Long teamId,
                                  @Nullable Long competitionId) {
        if (teamId != null) {
            Long playerTeamId = player.getTeam() != null ? player.getTeam().getId() : null;
            if (!Objects.equals(teamId, playerTeamId)) {
                return false;
            }
        }

        if (competitionId != null) {
            Long playerCompetitionId = player.getTeam() != null && player.getTeam().getCompetition() != null
                    ? player.getTeam().getCompetition().getId()
                    : null;
            if (!Objects.equals(competitionId, playerCompetitionId)) {
                return false;
            }
        }

        String normalizedPosition = normalize(position);
        if (normalizedPosition != null && !normalizedPosition.equals(normalize(player.getPosition()))) {
            return false;
        }

        String normalizedSearch = normalize(search);
        if (normalizedSearch == null) {
            return true;
        }

        return contains(player.getName(), normalizedSearch)
                || contains(player.getNationality(), normalizedSearch)
                || contains(player.getPosition(), normalizedSearch)
                || contains(player.getTeam() != null ? player.getTeam().getName() : null, normalizedSearch)
                || contains(resolveCompetitionName(player), normalizedSearch);
    }

    private boolean matchesTransfer(Transfer transfer,
                                    @Nullable String search,
                                    @Nullable String season) {
        String normalizedSeason = normalize(season);
        if (normalizedSeason != null && !normalizedSeason.equals(normalize(transfer.getSeason()))) {
            return false;
        }

        String normalizedSearch = normalize(search);
        if (normalizedSearch == null) {
            return true;
        }

        return contains(transfer.getPlayer().getName(), normalizedSearch)
                || contains(transfer.getSeason(), normalizedSearch)
                || contains(transfer.getFromTeam() != null ? transfer.getFromTeam().getName() : null, normalizedSearch)
                || contains(transfer.getToTeam() != null ? transfer.getToTeam().getName() : null, normalizedSearch);
    }

    private boolean matchesTeam(Team team,
                                @Nullable String search,
                                @Nullable Long competitionId) {
        if (competitionId != null) {
            Long teamCompetitionId = team.getCompetition() != null ? team.getCompetition().getId() : null;
            if (!Objects.equals(competitionId, teamCompetitionId)) {
                return false;
            }
        }

        String normalizedSearch = normalize(search);
        if (normalizedSearch == null) {
            return true;
        }

        return contains(team.getName(), normalizedSearch)
                || contains(team.getCountry(), normalizedSearch)
                || contains(team.getLeague(), normalizedSearch)
                || contains(team.getCompetition() != null ? team.getCompetition().getName() : null, normalizedSearch);
    }

    private Comparator<Player> buildPlayerComparator(@Nullable String sortBy,
                                                     @Nullable String direction) {
        Map<String, Comparator<Player>> comparators = new HashMap<>();
        comparators.put("name", Comparator.comparing(
                player -> valueOrEmpty(player.getName()),
                String.CASE_INSENSITIVE_ORDER
        ));
        comparators.put("age", Comparator.comparing(player -> valueOrZero(player.getAge())));
        comparators.put("marketvalue", Comparator.comparing(player -> valueOrZero(player.getMarketValue())));
        comparators.put("rating", Comparator.comparing(player -> valueOrZero(player.getRating())));
        comparators.put("goals", Comparator.comparing(player -> valueOrZero(player.getGoalsScored())));
        comparators.put("assists", Comparator.comparing(player -> valueOrZero(player.getAssists())));

        Comparator<Player> comparator = comparators.getOrDefault(
                normalize(sortBy),
                comparators.get("marketvalue")
        ).thenComparing(player -> valueOrEmpty(player.getName()), String.CASE_INSENSITIVE_ORDER);

        if (!"asc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private PlayerResponse toPlayerResponse(Player player) {
        TeamSummaryResponse teamSummary = toTeamSummary(player.getTeam());
        CompetitionSummaryResponse competitionSummary = player.getTeam() != null
                ? toCompetitionSummary(player.getTeam().getCompetition())
                : null;

        String teamName = teamSummary != null ? teamSummary.getName() : null;
        String league = competitionSummary != null && competitionSummary.getName() != null
                ? competitionSummary.getName()
                : player.getTeam() != null ? player.getTeam().getLeague() : null;

        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getFirstname(),
                player.getLastname(),
                player.getAge(),
                player.getNationality(),
                player.getPosition(),
                valueOrZero(player.getMarketValue()),
                teamSummary,
                competitionSummary,
                teamName,
                teamName,
                league,
                player.getPhotoUrl(),
                player.getPhotoUrl(),
                resolvePlayerDescription(player),
                valueOrZero(player.getAppearances()),
                valueOrZero(player.getGoalsScored()),
                valueOrZero(player.getAssists()),
                valueOrZero(player.getMinutesPlayed()),
                valueOrZero(player.getYellowCards()),
                valueOrZero(player.getRedCards()),
                valueOrZero(player.getRating()),
                player.getSpecialAbility()
        );
    }

    private PlayerSeasonStatResponse toPlayerSeasonStatResponse(PlayerStat stat) {
        return new PlayerSeasonStatResponse(
                stat.getId(),
                stat.getSeason(),
                toCompetitionSummary(stat.getCompetition()),
                toTeamSummary(stat.getTeam()),
                valueOrZero(stat.getAppearances()),
                valueOrZero(stat.getGoals()),
                valueOrZero(stat.getAssists()),
                valueOrZero(stat.getMinutes()),
                valueOrZero(stat.getRating())
        );
    }

    private TransferResponse toTransferResponse(Transfer transfer) {
        Long marketValue = transfer.getPlayer().getMarketValue() != null
                ? transfer.getPlayer().getMarketValue()
                : 0L;

        return new TransferResponse(
                transfer.getId(),
                transfer.getPlayer().getId(),
                transfer.getPlayer().getName(),
                transfer.getPlayer().getPhotoUrl(),
                marketValue,
                toTeamSummary(transfer.getFromTeam()),
                toTeamSummary(transfer.getToTeam()),
                transfer.getTransferFee(),
                transfer.getTransferType(),
                formatFee(transfer.getTransferFee()),
                transfer.getTransferDate(),
                transfer.getSeason()
        );
    }

    private TeamResponse toTeamResponse(Team team, List<Player> players) {
        long totalMarketValue = players.stream()
                .map(Player::getMarketValue)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getCountry(),
                team.getLeague(),
                team.getLogoUrl(),
                toCompetitionSummary(team.getCompetition()),
                players.size(),
                totalMarketValue
        );
    }

    private CompetitionSummaryResponse toCompetitionSummary(@Nullable Competition competition) {
        if (competition == null) {
            return null;
        }

        return new CompetitionSummaryResponse(
                competition.getId(),
                competition.getName(),
                competition.getCountry(),
                competition.getLogoUrl()
        );
    }

    private TeamSummaryResponse toTeamSummary(@Nullable Team team) {
        if (team == null) {
            return null;
        }

        return new TeamSummaryResponse(
                team.getId(),
                team.getName(),
                team.getCountry(),
                team.getLeague(),
                team.getLogoUrl()
        );
    }

    private void copyPlayerFields(PlayerResponse source, PlayerDetailResponse target) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setFirstname(source.getFirstname());
        target.setLastname(source.getLastname());
        target.setAge(source.getAge());
        target.setNationality(source.getNationality());
        target.setPosition(source.getPosition());
        target.setMarketValue(source.getMarketValue());
        target.setTeamSummary(source.getTeamSummary());
        target.setCompetition(source.getCompetition());
        target.setTeam(source.getTeam());
        target.setClub(source.getClub());
        target.setLeague(source.getLeague());
        target.setImage(source.getImage());
        target.setPhoto(source.getPhoto());
        target.setDescription(source.getDescription());
        target.setAppearances(source.getAppearances());
        target.setGoalsScored(source.getGoalsScored());
        target.setAssists(source.getAssists());
        target.setMinutesPlayed(source.getMinutesPlayed());
        target.setYellowCards(source.getYellowCards());
        target.setRedCards(source.getRedCards());
        target.setRating(source.getRating());
        target.setSpecialAbility(source.getSpecialAbility());
    }

    private String resolveCompetitionName(Player player) {
        if (player.getTeam() == null) {
            return null;
        }

        if (player.getTeam().getCompetition() != null && player.getTeam().getCompetition().getName() != null) {
            return player.getTeam().getCompetition().getName();
        }

        return player.getTeam().getLeague();
    }

    private String resolvePlayerDescription(Player player) {
        if (player.getDescription() != null && !player.getDescription().isBlank()) {
            return player.getDescription();
        }

        String position = player.getPosition() != null ? player.getPosition().toLowerCase(Locale.ROOT) : "player";
        String club = player.getTeam() != null ? player.getTeam().getName() : "club football";
        return player.getName() + " is a featured " + position + " playing for " + club + ".";
    }

    private String formatFee(@Nullable Long fee) {
        if (fee == null || fee <= 0) {
            return "Free";
        }

        if (fee >= 1_000_000_000L) {
            return String.format(Locale.US, "EUR %.1fB", fee / 1_000_000_000.0);
        }
        if (fee >= 1_000_000L) {
            return String.format(Locale.US, "EUR %.1fM", fee / 1_000_000.0);
        }
        if (fee >= 1_000L) {
            return String.format(Locale.US, "EUR %.0fK", fee / 1_000.0);
        }

        return "EUR " + NumberFormat.getIntegerInstance(Locale.US).format(fee);
    }

    private String normalize(@Nullable String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean contains(@Nullable String value, @NonNull String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private Integer valueOrZero(@Nullable Integer value) {
        return value != null ? value : 0;
    }

    private Long valueOrZero(@Nullable Long value) {
        return value != null ? value : 0L;
    }

    private BigDecimal valueOrZero(@Nullable BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String valueOrEmpty(@Nullable String value) {
        return value != null ? value : "";
    }
}
