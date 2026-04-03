import React, { useEffect, useState } from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";
import { Link } from "react-router-dom";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const LEAGUES = [
  { id: 39, name: "Premier League", flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿" },
  { id: 140, name: "La Liga", flag: "🇪🇸" },
  { id: 135, name: "Serie A", flag: "🇮🇹" },
  { id: 78, name: "Bundesliga", flag: "🇩🇪" },
  { id: 61, name: "Ligue 1", flag: "🇫🇷" },
  { id: 2, name: "Champions League", flag: "🏆" },
];

const FormBadge = ({ result }) => {
  const colors = { W: "bg-green-600", D: "bg-yellow-500", L: "bg-red-600" };
  return (
    <span className={`inline-block w-5 h-5 rounded-full text-white text-[10px] font-bold grid place-items-center ${colors[result] || "bg-[#333]"}`}>
      {result}
    </span>
  );
};

const StandingsPage = () => {
  const [selectedLeague, setSelectedLeague] = useState(LEAGUES[0]);
  const [standings, setStandings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [empty, setEmpty] = useState(false);

  useEffect(() => {
    setLoading(true);
    setEmpty(false);
    fetch(`${API_BASE}/api/standings?league=${selectedLeague.id}`)
      .then((r) => r.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : [];
        setStandings(list);
        setEmpty(list.length === 0);
        setLoading(false);
      })
      .catch(() => {
        setEmpty(true);
        setLoading(false);
      });
  }, [selectedLeague]);

  const descriptionColor = (desc) => {
    if (!desc) return "";
    const d = desc.toLowerCase();
    if (d.includes("champions league")) return "border-l-4 border-blue-500";
    if (d.includes("europa league")) return "border-l-4 border-orange-400";
    if (d.includes("relegation")) return "border-l-4 border-red-500";
    return "";
  };

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="max-w-5xl mx-auto px-4 sm:px-6 py-8 flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-white mb-2">Standings</h1>
        <p className="text-[#B3B3B3] mb-6">Live league tables updated from API-Football.</p>

        {/* League tabs */}
        <div className="flex flex-wrap gap-2 mb-6">
          {LEAGUES.map((l) => (
            <button
              key={l.id}
              onClick={() => setSelectedLeague(l)}
              className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                selectedLeague.id === l.id
                  ? "bg-[#FF6B00] text-white"
                  : "bg-[#1A1A1A] text-[#B3B3B3] hover:bg-[#262626]"
              }`}
            >
              <span>{l.flag}</span>
              <span className="hidden sm:inline">{l.name}</span>
            </button>
          ))}
        </div>

        {loading && (
          <div className="text-center py-20 text-[#B3B3B3]">
            <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
            <p>Loading standings...</p>
          </div>
        )}

        {empty && !loading && (
          <div className="text-center py-20 text-[#B3B3B3] bg-[#1A1A1A] rounded-2xl border border-[#262626]">
            <p className="text-3xl mb-3">📊</p>
            <p className="text-lg font-semibold text-white mb-1">No standings available</p>
            <p className="text-sm">API key may not be configured or limit reached.</p>
          </div>
        )}

        {!loading && standings.length > 0 && (
          <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A]">
            <table className="w-full min-w-[640px]">
              <thead className="bg-[#0F0F0F]">
                <tr className="text-[#FF6B00] text-xs font-semibold text-left">
                  <th className="px-4 py-3 w-8">#</th>
                  <th className="px-4 py-3">Club</th>
                  <th className="px-4 py-3 text-center">MP</th>
                  <th className="px-4 py-3 text-center">W</th>
                  <th className="px-4 py-3 text-center">D</th>
                  <th className="px-4 py-3 text-center">L</th>
                  <th className="px-4 py-3 text-center">GF</th>
                  <th className="px-4 py-3 text-center">GA</th>
                  <th className="px-4 py-3 text-center">GD</th>
                  <th className="px-4 py-3 text-center font-bold">Pts</th>
                  <th className="px-4 py-3">Form</th>
                </tr>
              </thead>
              <tbody>
                {standings.map((row, i) => (
                  <tr
                    key={row.teamId || i}
                    className={`border-t border-[#0F0F0F] hover:bg-[#262626] transition-colors text-[#B3B3B3] ${descriptionColor(row.description)}`}
                  >
                    <td className="px-4 py-3 text-white font-bold text-sm">{row.rank}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-3">
                        {row.teamLogo && (
                          <img src={row.teamLogo} alt={row.teamName} className="w-6 h-6 object-contain" />
                        )}
                        <span className="text-white font-medium text-sm">{row.teamName}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-center text-sm">{row.played}</td>
                    <td className="px-4 py-3 text-center text-sm text-green-400">{row.win}</td>
                    <td className="px-4 py-3 text-center text-sm text-yellow-400">{row.draw}</td>
                    <td className="px-4 py-3 text-center text-sm text-red-400">{row.lose}</td>
                    <td className="px-4 py-3 text-center text-sm">{row.goalsFor}</td>
                    <td className="px-4 py-3 text-center text-sm">{row.goalsAgainst}</td>
                    <td className="px-4 py-3 text-center text-sm">{row.goalDifference > 0 ? `+${row.goalDifference}` : row.goalDifference}</td>
                    <td className="px-4 py-3 text-center text-white font-extrabold text-sm">{row.points}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-0.5">
                        {(row.form || "").split("").slice(-5).map((r, j) => (
                          <FormBadge key={j} result={r} />
                        ))}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Legend */}
        {standings.length > 0 && (
          <div className="flex flex-wrap gap-4 mt-4 text-xs text-[#B3B3B3]">
            <span className="flex items-center gap-1"><span className="w-3 h-3 bg-blue-500 rounded-sm inline-block"></span> Champions League</span>
            <span className="flex items-center gap-1"><span className="w-3 h-3 bg-orange-400 rounded-sm inline-block"></span> Europa League</span>
            <span className="flex items-center gap-1"><span className="w-3 h-3 bg-red-500 rounded-sm inline-block"></span> Relegation</span>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default StandingsPage;
