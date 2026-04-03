import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Header from "../Components/Header";
import Hero from "../Components/Hero";
import LatestNews from "../Components/LatestNews";
import TrendPlayers from "../Components/TrendPlayers";
import Footer from "../Components/Footer";
import { playerSchemas } from "../data/playerSchemas";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "https://football-talks.onrender.com";

const normalizeName = (value) =>
  String(value || "").trim().toLowerCase().replace(/\s+/g, " ");

const curatedPlayersByName = new Map(
  playerSchemas.map((player) => [normalizeName(player.name), player])
);

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
    });
};

const HomePage = () => {
  const [trendingPlayers, setTrendingPlayers] = useState(playerSchemas.slice(0, 8));
  const [stats, setStats] = useState({ players: null, clubs: null, transfers: null });

  useEffect(() => {
    // Fetch players for trending section
    fetch(`${API_BASE}/api/players`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : [];
        if (list.length > 0) {
          setTrendingPlayers(enrichPlayers(list).slice(0, 8));
          setStats((prev) => ({ ...prev, players: list.length }));
        }
      })
      .catch(() => {});

    // Fetch team count
    fetch(`${API_BASE}/api/teams`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || [];
        if (list.length > 0) setStats((prev) => ({ ...prev, clubs: list.length }));
      })
      .catch(() => {});

    // Fetch transfer count
    fetch(`${API_BASE}/api/transfers`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || [];
        if (list.length > 0) setStats((prev) => ({ ...prev, transfers: list.length }));
      })
      .catch(() => {});
  }, []);

  const getPlayerName = (player, index) =>
    player?.name || player?.playerName || `Player ${index + 1}`;
  const getPlayerRole = (player) => player?.position || player?.role || "Player";
  const getPlayerDescription = (player) =>
    player?.description || player?.bio || "Featured football player.";
  const getPlayerImage = (player) =>
    player?.image || player?.imageUrl || player?.photo ||
    "https://images.unsplash.com/photo-1565992441121-4367c2967103?auto=format&fit=crop&w=1200&q=80";
  const getPlayerImagePosition = (player) => player?.imagePosition || player?.image_position || "center 14%";
  const getPlayerClub = (player) => player?.club || player?.team || "Club Not Listed";
  const getPlayerAge = (player) => player?.age || "N/A";
  const getPlayerNationality = (player) => player?.nationality || player?.country || "Unknown";

  return (
    <div className="min-h-screen flex flex-col bg-[#1a1717]">
      <Header />
      <main className="flex-1">
        <Hero stats={stats} />

        {/* Latest News */}
        <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 py-10">
          <div className="w-full flex items-center justify-between gap-4 mb-6">
            <h1 className="text-2xl font-bold text-[#FFFFFF]">Latest News</h1>
            <Link to="/news" className="text-base sm:text-lg text-[#FF6B00] hover:text-[#FF8533]">
              View all
            </Link>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 justify-items-center">
            <LatestNews
              title="Latest News"
              news="Breaking News"
              description="This is a sample news description."
              src="https://library.sportingnews.com/styles/crop_style_16_9_desktop_webp/s3/2025-09/football-today_ftr_2025.png.webp?itok=vRnwPop2"
              delay="80ms"
            />
            <LatestNews
              title="Match Update"
              news="Live Scores"
              description="Stay tuned for live match updates."
              src="https://media.assettype.com/thequint%2F2024-01%2Ff703755c-9220-4c79-ba2b-57f7475c5d81%2Fimage_2024_01_23_195237395.png?auto=format%2Ccompress&fmt=webp&width=720"
              delay="180ms"
            />
            <LatestNews
              title="Player Transfers"
              news="Transfer News"
              description="Get the latest on player transfers."
              src="https://a57.foxsports.com/statics.foxsports.com/www.foxsports.com/content/uploads/2026/01/548/308/friedman1.jpg?ve=1&tl=1"
              delay="280ms"
            />
          </div>
        </section>

        {/* Trending Players */}
        <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 py-10">
          <div className="w-full flex items-center justify-between gap-4 mb-6">
            <h1 className="text-2xl font-bold text-[#FFFFFF]">Trending Players</h1>
            <Link to="/players" className="text-base sm:text-lg text-[#FF6B00] hover:text-[#FF8533]">
              View all
            </Link>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4 justify-items-center">
            {trendingPlayers.map((player, index) => (
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
                appearances={player?.appearances ?? player?.apps ?? 0}
                goalsScored={player?.goalsScored ?? player?.goals ?? 0}
                assists={player?.assists ?? 0}
                rating={player?.rating ?? "-"}
                specialAbility={player?.specialAbility || player?.special_ability || ""}
                delay={`${120 + index * 100}ms`}
              />
            ))}
          </div>
        </section>

        {/* Recent Transfers */}
        <RecentTransfersSection />
      </main>
      <Footer />
    </div>
  );
};

// Inline real transfers section replacing the hardcoded TransferTable
const RecentTransfersSection = () => {
  const [transfers, setTransfers] = useState([]);

  useEffect(() => {
    fetch(`${API_BASE}/api/transfers`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || [];
        setTransfers(list.slice(0, 6));
      })
      .catch(() => {});
  }, []);

  const formatFee = (fee) => {
    if (!fee || fee === 0) return "Free";
    if (fee >= 1_000_000) return `€${(fee / 1_000_000).toFixed(0)}M`;
    return `€${fee}`;
  };

  if (transfers.length === 0) return null;

  return (
    <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 py-10">
      <div className="w-full flex items-center justify-between gap-4 mb-6">
        <h1 className="text-2xl font-bold text-[#FFFFFF]">Recent Transfers</h1>
        <Link to="/transfers" className="text-base sm:text-lg text-[#FF6B00] hover:text-[#FF8533]">
          View all
        </Link>
      </div>
      <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A]">
        <table className="w-full min-w-[480px]">
          <thead className="bg-[#0F0F0F]">
            <tr className="text-left text-[#FF6B00] text-sm">
              <th className="px-6 py-3">Player</th>
              <th className="px-6 py-3">From</th>
              <th className="px-6 py-3">To</th>
              <th className="px-6 py-3">Fee</th>
            </tr>
          </thead>
          <tbody>
            {transfers.map((t, i) => (
              <tr key={t.id || i} className="border-t border-[#0F0F0F] hover:bg-[#262626] text-[#B3B3B3] transition-colors">
                <td className="px-6 py-3 font-medium text-white text-sm">
                  {t.playerName || t.player?.name || "Unknown"}
                </td>
                <td className="px-6 py-3 text-sm">{t.fromClub || t.fromTeam?.name || "—"}</td>
                <td className="px-6 py-3 text-sm">{t.toClub || t.toTeam?.name || "—"}</td>
                <td className="px-6 py-3 text-[#FF6B00] font-semibold text-sm">
                  {formatFee(t.transferFee || t.fee)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default HomePage;
