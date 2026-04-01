import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { getPlayers } from "../Services/api";
import Header from "../Components/Header";
import Footer from "../Components/Footer";
import TrendPlayers from "../Components/TrendPlayers";
import { playerSchemas } from "../data/playerSchemas";

const normalizeName = (value) =>
  String(value || "")
    .trim()
    .toLowerCase()
    .replace(/\s+/g, " ");

const curatedPlayersByName = new Map(
  playerSchemas.map((player) => [normalizeName(player.name), player])
);

const normalizePlayers = (data) => {
  if (Array.isArray(data)) return data;
  if (!data || typeof data !== "object") return [];

  const candidates = [data.players, data.data, data.items, data.results, data.response];
  const list = candidates.find(Array.isArray);
  return Array.isArray(list) ? list : [];
};

const enrichPlayers = (players) => {
  const seen = new Set();

  return players
    .map((player) => {
      if (!player || typeof player !== "object") return player;

      const curated = curatedPlayersByName.get(normalizeName(player.name));
      return curated ? { ...player, ...curated } : player;
    })
    .filter((player) => {
      const key = normalizeName(player?.name);
      if (!key || seen.has(key)) return false;
      seen.add(key);
      return true;
    })
    .sort((left, right) => {
      const leftCuratedIndex = playerSchemas.findIndex(
        (player) => normalizeName(player.name) === normalizeName(left?.name)
      );
      const rightCuratedIndex = playerSchemas.findIndex(
        (player) => normalizeName(player.name) === normalizeName(right?.name)
      );

      if (leftCuratedIndex !== -1 && rightCuratedIndex !== -1) {
        return leftCuratedIndex - rightCuratedIndex;
      }
      if (leftCuratedIndex !== -1) return -1;
      if (rightCuratedIndex !== -1) return 1;

      return String(left?.name || "").localeCompare(String(right?.name || ""));
    });
};

function Players() {
  const [searchParams] = useSearchParams();
  const [players, setPlayers] = useState(playerSchemas);
  const searchTerm = searchParams.get("search")?.trim() || "";

  useEffect(() => {
    getPlayers()
      .then((data) => {
        const normalized = normalizePlayers(data);
        if (normalized.length > 0) {
          setPlayers(enrichPlayers(normalized));
        }
      })
      .catch(() => {
        // Keep bundled player data when API is unavailable.
      });
  }, []);

  const getPlayerName = (player, index) => {
    if (typeof player === "string") return player;
    if (player && typeof player === "object") {
      return player.name || player.playerName || player.fullName || `Player ${index + 1}`;
    }
    return `Player ${index + 1}`;
  };

  const getPlayerRole = (player) => {
    if (!player || typeof player !== "object") return "Player";
    return player.position || player.role || player.type || "Player";
  };

  const getPlayerDescription = (player) => {
    if (!player || typeof player !== "object") return "Featured football player.";
    return player.description || player.bio || player.info || "Featured football player.";
  };

  const getPlayerImage = (player) => {
    if (!player || typeof player !== "object") {
      return "https://images.unsplash.com/photo-1565992441121-4367c2967103?auto=format&fit=crop&w=1200&q=80";
    }

    return (
      player.image ||
      player.imageUrl ||
      player.photo ||
      player.avatar ||
      "https://images.unsplash.com/photo-1565992441121-4367c2967103?auto=format&fit=crop&w=1200&q=80"
    );
  };

  const getPlayerImagePosition = (player) => {
    if (!player || typeof player !== "object") return "center 14%";

    const explicitPosition = player.imagePosition || player.image_position;
    if (explicitPosition) return explicitPosition;

    const role = String(player.position || player.role || player.type || "").toLowerCase();
    if (role.includes("goal")) return "center 16%";
    if (role.includes("def")) return "center 15%";
    return "center 14%";
  };

  const getPlayerClub = (player) => {
    if (!player || typeof player !== "object") return "Club Not Listed";
    return player.club || player.team || player.currentClub || "Club Not Listed";
  };

  const getPlayerAge = (player) => {
    if (!player || typeof player !== "object") return "N/A";
    return player.age || player.playerAge || "N/A";
  };

  const getPlayerNationality = (player) => {
    if (!player || typeof player !== "object") return "Unknown";
    return player.nationality || player.country || player.nation || "Unknown";
  };

  const getPlayerAppearances = (player) => {
    if (!player || typeof player !== "object") return 0;
    return player.appearances ?? player.apps ?? 0;
  };

  const getPlayerGoals = (player) => {
    if (!player || typeof player !== "object") return 0;
    return player.goalsScored ?? player.goals_scored ?? player.goals ?? 0;
  };

  const getPlayerAssists = (player) => {
    if (!player || typeof player !== "object") return 0;
    return player.assists ?? 0;
  };

  const getPlayerRating = (player) => {
    if (!player || typeof player !== "object") return "-";
    return player.rating ?? "-";
  };

  const getPlayerSpecialAbility = (player) => {
    if (!player || typeof player !== "object") return "";
    return player.specialAbility || player.special_ability || "";
  };

  const matchesSearch = (player) => {
    if (!searchTerm) return true;

    const searchableText = [
      getPlayerName(player, 0),
      getPlayerRole(player),
      getPlayerDescription(player),
      getPlayerClub(player),
      getPlayerNationality(player),
      player?.specialAbility,
    ]
      .filter(Boolean)
      .join(" ")
      .toLowerCase();

    return searchableText.includes(searchTerm.toLowerCase());
  };

  const filteredPlayers = players.filter(matchesSearch);

  return (
    <div className="players-page-bg min-h-screen flex flex-col">
      <Header />
      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-8 sm:py-10 flex-1 w-full">
        <h1 className="players-title text-3xl sm:text-4xl font-bold text-[#FFFFFF] mb-3">
          Top Players
        </h1>
        <p className="players-subtitle text-[#B3B3B3] mb-8">
          {searchTerm
            ? `Showing ${filteredPlayers.length} result${filteredPlayers.length === 1 ? "" : "s"} for "${searchTerm}".`
            : `Discover featured football players with live data. ${players.length} profiles loaded.`}
        </p>
        {filteredPlayers.length === 0 ? (
          <div className="rounded-2xl border border-[#1A1A1A] bg-[#121821] px-6 py-10 text-center text-[#B3B3B3]">
            No players matched "{searchTerm}". Try a player name, club, nationality, or position.
          </div>
        ) : (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 justify-items-center">
            {filteredPlayers.map((player, index) => (
            <TrendPlayers
              key={`${getPlayerName(player, index)}-${index}`}
              title={getPlayerName(player, index)}
              news={getPlayerRole(player)}
              description={getPlayerDescription(player)}
              src={getPlayerImage(player)}
              imagePosition={getPlayerImagePosition(player)}
              club={getPlayerClub(player)}
              age={getPlayerAge(player)}
              nationality={getPlayerNationality(player)}
              appearances={getPlayerAppearances(player)}
              goalsScored={getPlayerGoals(player)}
              assists={getPlayerAssists(player)}
              rating={getPlayerRating(player)}
              specialAbility={getPlayerSpecialAbility(player)}
              delay={`${index * 100}ms`}
            />
            ))}
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
}

export default Players;
