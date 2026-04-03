import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const StatBox = ({ label, value }) => (
  <div className="bg-[#0F0F0F] rounded-xl p-4 text-center">
    <p className="text-2xl font-extrabold text-[#FF6B00]">{value ?? "—"}</p>
    <p className="text-xs text-[#B3B3B3] mt-1">{label}</p>
  </div>
);

const formatFee = (fee) => {
  if (!fee || fee === 0) return "Free";
  if (fee >= 1_000_000) return `€${(fee / 1_000_000).toFixed(1)}M`;
  if (fee >= 1_000) return `€${(fee / 1_000).toFixed(0)}K`;
  return `€${fee}`;
};

const PlayerDetailPage = () => {
  const { id } = useParams();
  const [player, setPlayer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    fetch(`${API_BASE}/api/players/${id}`)
      .then((res) => {
        if (!res.ok) throw new Error();
        return res.json();
      })
      .then((data) => {
        setPlayer(data);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  }, [id]);

  if (loading) return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="flex-1 flex items-center justify-center">
        <div className="text-center text-[#B3B3B3]">
          <div className="inline-block w-10 h-10 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
          <p>Loading player...</p>
        </div>
      </main>
    </div>
  );

  if (error || !player) return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="flex-1 flex items-center justify-center text-[#B3B3B3]">
        <div className="text-center">
          <p className="text-4xl mb-4">😕</p>
          <p className="text-xl mb-4">Player not found.</p>
          <Link to="/players" className="text-[#FF6B00] hover:text-[#FF8533]">← Back to Players</Link>
        </div>
      </main>
    </div>
  );

  const photoUrl = player.image || player.photo || player.photoUrl;
  const fallbackImg = `https://ui-avatars.com/api/?name=${encodeURIComponent(player.name)}&background=111827&color=ffffff&bold=true&size=512`;
  const teamName = player.teamSummary?.name || player.team || player.club || "—";
  const league = player.competition?.name || player.teamSummary?.league || player.league || "—";

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="max-w-5xl mx-auto px-4 sm:px-6 py-8 flex-1 w-full">

        {/* Back */}
        <Link to="/players" className="text-[#FF6B00] hover:text-[#FF8533] text-sm mb-6 inline-block">
          ← Back to Players
        </Link>

        {/* Hero Card */}
        <div className="bg-[#1A1A1A] rounded-2xl border border-[#262626] overflow-hidden mb-6">
          <div className="relative h-32 bg-gradient-to-r from-[#FF6B00]/20 to-[#0F0F0F]" />
          <div className="px-6 pb-6 -mt-16 flex flex-col sm:flex-row gap-6 items-start">
            <img
              src={photoUrl || fallbackImg}
              alt={player.name}
              className="w-28 h-28 rounded-2xl object-cover border-4 border-[#1A1A1A] shadow-lg bg-[#0F0F0F]"
              style={{ objectPosition: "center 10%" }}
              onError={(e) => { e.currentTarget.src = fallbackImg; }}
            />
            <div className="pt-16 sm:pt-12 flex-1">
              <div className="flex flex-wrap items-center gap-2 mb-1">
                <span className="text-xs bg-[#FF6B00]/20 text-[#FF6B00] px-2 py-0.5 rounded-full font-semibold">
                  {player.position || "Player"}
                </span>
                {player.nationality && (
                  <span className="text-xs bg-[#262626] text-[#B3B3B3] px-2 py-0.5 rounded-full">
                    {player.nationality}
                  </span>
                )}
              </div>
              <h1 className="text-3xl sm:text-4xl font-extrabold text-white">{player.name}</h1>
              <p className="text-[#B3B3B3] mt-1">
                {teamName} {league !== "—" && `· ${league}`}
                {player.age && ` · Age ${player.age}`}
              </p>
              {player.marketValue > 0 && (
                <p className="text-[#FF6B00] font-bold text-xl mt-2">
                  Market Value: {formatFee(player.marketValue)}
                </p>
              )}
              {player.description && (
                <p className="text-[#B3B3B3] text-sm mt-3 max-w-xl leading-relaxed">{player.description}</p>
              )}
            </div>
          </div>
        </div>

        {/* Stats */}
        <h2 className="text-xl font-bold text-white mb-4">Season Stats</h2>
        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-6 gap-3 mb-8">
          <StatBox label="Appearances" value={player.appearances} />
          <StatBox label="Goals" value={player.goalsScored} />
          <StatBox label="Assists" value={player.assists} />
          <StatBox label="Rating" value={player.rating} />
          <StatBox label="Yellow Cards" value={player.yellowCards} />
          <StatBox label="Red Cards" value={player.redCards} />
        </div>

        {/* Stats History */}
        {player.statsHistory?.length > 0 && (
          <>
            <h2 className="text-xl font-bold text-white mb-4">Stats History</h2>
            <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A] mb-8">
              <table className="w-full min-w-[500px]">
                <thead className="bg-[#0F0F0F]">
                  <tr className="text-[#FF6B00] text-sm text-left">
                    <th className="px-4 py-3">Season</th>
                    <th className="px-4 py-3">Club</th>
                    <th className="px-4 py-3">Apps</th>
                    <th className="px-4 py-3">Goals</th>
                    <th className="px-4 py-3">Assists</th>
                    <th className="px-4 py-3">Rating</th>
                  </tr>
                </thead>
                <tbody>
                  {player.statsHistory.map((s, i) => (
                    <tr key={i} className="border-t border-[#0F0F0F] text-[#B3B3B3] hover:bg-[#262626] transition-colors">
                      <td className="px-4 py-3 text-white font-medium">{s.season}</td>
                      <td className="px-4 py-3">{s.teamName || "—"}</td>
                      <td className="px-4 py-3">{s.appearances ?? "—"}</td>
                      <td className="px-4 py-3">{s.goals ?? "—"}</td>
                      <td className="px-4 py-3">{s.assists ?? "—"}</td>
                      <td className="px-4 py-3">{s.rating ?? "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {/* Transfer History */}
        {player.transferHistory?.length > 0 && (
          <>
            <h2 className="text-xl font-bold text-white mb-4">Transfer History</h2>
            <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A] mb-8">
              <table className="w-full min-w-[500px]">
                <thead className="bg-[#0F0F0F]">
                  <tr className="text-[#FF6B00] text-sm text-left">
                    <th className="px-4 py-3">Date</th>
                    <th className="px-4 py-3">From</th>
                    <th className="px-4 py-3">To</th>
                    <th className="px-4 py-3">Fee</th>
                    <th className="px-4 py-3">Type</th>
                  </tr>
                </thead>
                <tbody>
                  {player.transferHistory.map((t, i) => (
                    <tr key={i} className="border-t border-[#0F0F0F] text-[#B3B3B3] hover:bg-[#262626] transition-colors">
                      <td className="px-4 py-3 text-white">{t.transferDate || "—"}</td>
                      <td className="px-4 py-3">{t.fromTeam?.name || "—"}</td>
                      <td className="px-4 py-3">{t.toTeam?.name || "—"}</td>
                      <td className="px-4 py-3 text-[#FF6B00] font-semibold">{formatFee(t.transferFee)}</td>
                      <td className="px-4 py-3 capitalize">{t.transferType || "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {player.specialAbility && (
          <div className="bg-[#1A1A1A] rounded-xl border border-[#262626] p-4">
            <p className="text-xs text-[#FF6B00] font-semibold uppercase tracking-wider mb-1">Special Ability</p>
            <p className="text-white">{player.specialAbility}</p>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default PlayerDetailPage;
