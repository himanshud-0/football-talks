import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const formatValue = (val) => {
  if (!val || val === 0) return "—";
  if (val >= 1_000_000) return `€${(val / 1_000_000).toFixed(1)}M`;
  if (val >= 1_000) return `€${(val / 1_000).toFixed(0)}K`;
  return `€${val}`;
};

const ClubDetailPage = () => {
  const { id } = useParams();
  const [team, setTeam] = useState(null);
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    // Fetch team info and players for this team in parallel
    Promise.all([
      fetch(`${API_BASE}/api/teams/${id}`).then((r) => r.json()).catch(() => null),
      fetch(`${API_BASE}/api/players?teamId=${id}&limit=50`).then((r) => r.json()).catch(() => []),
    ]).then(([teamData, playersData]) => {
      setTeam(teamData);
      const list = Array.isArray(playersData) ? playersData : [];
      setPlayers(list);
      setLoading(false);
    }).catch(() => {
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
          <p>Loading club...</p>
        </div>
      </main>
    </div>
  );

  if (error || !team) return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="flex-1 flex items-center justify-center text-[#B3B3B3]">
        <div className="text-center">
          <p className="text-4xl mb-4">😕</p>
          <p className="text-xl mb-4">Club not found.</p>
          <Link to="/leagues" className="text-[#FF6B00] hover:text-[#FF8533]">← Back to Clubs</Link>
        </div>
      </main>
    </div>
  );

  const byPosition = players.reduce((acc, p) => {
    const pos = p.position || "Other";
    if (!acc[pos]) acc[pos] = [];
    acc[pos].push(p);
    return acc;
  }, {});

  const positionOrder = ["Goalkeeper", "Defender", "Midfielder", "Forward", "Other"];

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="max-w-5xl mx-auto px-4 sm:px-6 py-8 flex-1 w-full">

        <Link to="/leagues" className="text-[#FF6B00] hover:text-[#FF8533] text-sm mb-6 inline-block">
          ← Back to Clubs
        </Link>

        {/* Club Header */}
        <div className="bg-[#1A1A1A] rounded-2xl border border-[#262626] p-6 mb-6 flex flex-col sm:flex-row items-center sm:items-start gap-6">
          {team.logoUrl ? (
            <img src={team.logoUrl} alt={team.name} className="w-20 h-20 object-contain" />
          ) : (
            <div className="w-20 h-20 rounded-xl bg-[#0F0F0F] grid place-items-center text-3xl">🏟️</div>
          )}
          <div className="text-center sm:text-left">
            <h1 className="text-3xl font-extrabold text-white">{team.name}</h1>
            <p className="text-[#B3B3B3] mt-1">
              {team.country} · {team.competition?.name || team.league || "—"}
            </p>
            <div className="flex flex-wrap gap-4 mt-3 justify-center sm:justify-start">
              <div>
                <p className="text-xs text-[#B3B3B3]">Squad Size</p>
                <p className="text-[#FF6B00] font-bold text-lg">{team.playerCount || players.length}</p>
              </div>
              {team.totalMarketValue > 0 && (
                <div>
                  <p className="text-xs text-[#B3B3B3]">Squad Value</p>
                  <p className="text-[#FF6B00] font-bold text-lg">{formatValue(team.totalMarketValue)}</p>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Squad by Position */}
        {players.length > 0 ? (
          <>
            <h2 className="text-xl font-bold text-white mb-4">Squad ({players.length} players)</h2>
            {positionOrder.map((pos) => {
              const group = byPosition[pos];
              if (!group || group.length === 0) return null;
              return (
                <div key={pos} className="mb-6">
                  <h3 className="text-sm font-semibold text-[#FF6B00] uppercase tracking-wider mb-3">{pos}s</h3>
                  <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A]">
                    <table className="w-full min-w-[400px]">
                      <thead className="bg-[#0F0F0F]">
                        <tr className="text-[#B3B3B3] text-xs text-left">
                          <th className="px-4 py-2">Player</th>
                          <th className="px-4 py-2">Age</th>
                          <th className="px-4 py-2">Nationality</th>
                          <th className="px-4 py-2">Market Value</th>
                        </tr>
                      </thead>
                      <tbody>
                        {group.map((p, i) => {
                          const photo = p.image || p.photo || p.photoUrl;
                          const fallback = `https://ui-avatars.com/api/?name=${encodeURIComponent(p.name)}&background=111827&color=ffffff&size=64`;
                          return (
                            <tr key={p.id || i} className="border-t border-[#0F0F0F] hover:bg-[#262626] transition-colors">
                              <td className="px-4 py-2">
                                <Link to={`/players/${p.id}`} className="flex items-center gap-3 group">
                                  <img
                                    src={photo || fallback}
                                    alt={p.name}
                                    className="w-8 h-8 rounded-full object-cover bg-[#0F0F0F]"
                                    style={{ objectPosition: "center 10%" }}
                                    onError={(e) => { e.currentTarget.src = fallback; }}
                                  />
                                  <span className="text-white font-medium group-hover:text-[#FF6B00] transition-colors">{p.name}</span>
                                </Link>
                              </td>
                              <td className="px-4 py-2 text-[#B3B3B3] text-sm">{p.age || "—"}</td>
                              <td className="px-4 py-2 text-[#B3B3B3] text-sm">{p.nationality || "—"}</td>
                              <td className="px-4 py-2 text-[#FF6B00] font-semibold text-sm">{formatValue(p.marketValue)}</td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </div>
              );
            })}
          </>
        ) : (
          <div className="text-center py-16 text-[#B3B3B3] bg-[#1A1A1A] rounded-2xl border border-[#1A1A1A]">
            <p className="text-2xl mb-2">👥</p>
            <p>No squad data available yet.</p>
            <p className="text-sm mt-1">Run a sync to populate player data.</p>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default ClubDetailPage;
