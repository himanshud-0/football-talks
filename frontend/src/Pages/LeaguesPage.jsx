import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const LEAGUE_FLAGS = {
  "Premier League": "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
  "La Liga": "🇪🇸",
  "Serie A": "🇮🇹",
  "Bundesliga": "🇩🇪",
  "Ligue 1": "🇫🇷",
  "Champions League": "🏆",
  "Eredivisie": "🇳🇱",
  "Primeira Liga": "🇵🇹",
};

const LeaguesPage = () => {
  const [teams, setTeams] = useState([]);
  const [competitions, setCompetitions] = useState([]);
  const [selectedLeague, setSelectedLeague] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      fetch(`${API_BASE}/api/teams`).then((r) => r.json()).catch(() => []),
      fetch(`${API_BASE}/api/competitions`).then((r) => r.json()).catch(() => []),
    ]).then(([teamsData, compData]) => {
      setTeams(Array.isArray(teamsData) ? teamsData : []);
      const comps = Array.isArray(compData) ? compData : [];
      setCompetitions(comps);
      if (comps.length > 0) setSelectedLeague(comps[0].id || comps[0].competitionId);
      setLoading(false);
    });
  }, []);

  const fallbackLeagues = [
    { name: "Premier League", country: "England", clubs: 20 },
    { name: "La Liga", country: "Spain", clubs: 20 },
    { name: "Serie A", country: "Italy", clubs: 20 },
    { name: "Bundesliga", country: "Germany", clubs: 18 },
    { name: "Ligue 1", country: "France", clubs: 18 },
  ];

  const filteredTeams = selectedLeague
    ? teams.filter((t) => {
        const compId = t.competition?.id || t.competition?.competitionId || t.competitionId;
        return compId === selectedLeague;
      })
    : teams;

  const displayComps = competitions.length > 0 ? competitions : fallbackLeagues;

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="px-4 sm:px-6 py-8 sm:py-10 max-w-6xl mx-auto flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-white mb-2">Clubs & Leagues</h1>
        <p className="text-[#B3B3B3] mb-8">
          {loading ? "Loading..." : `${teams.length} clubs across ${competitions.length || 5} leagues.`}
        </p>

        {loading ? (
          <div className="text-center py-16 text-[#B3B3B3]">
            <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
            <p>Loading leagues...</p>
          </div>
        ) : (
          <div className="grid lg:grid-cols-[280px_1fr] gap-6">

            {/* League sidebar */}
            <div>
              <h2 className="text-sm font-semibold text-[#FF6B00] uppercase tracking-wider mb-3">Leagues</h2>
              <div className="bg-[#1A1A1A] rounded-xl border border-[#262626] overflow-hidden">
                {displayComps.map((comp, i) => {
                  const compId = comp.id || comp.competitionId || i;
                  const name = comp.name || comp.leagueName;
                  const country = comp.country || comp.nation || "—";
                  const flag = LEAGUE_FLAGS[name] || "🌍";
                  const isSelected = selectedLeague === compId;

                  return (
                    <button
                      key={compId}
                      onClick={() => setSelectedLeague(isSelected ? null : compId)}
                      className={`w-full text-left px-4 py-3 border-b border-[#0F0F0F] last:border-0 flex items-center gap-3 transition-colors ${
                        isSelected ? "bg-[#FF6B00]/10 text-[#FF6B00]" : "text-[#B3B3B3] hover:bg-[#262626]"
                      }`}
                    >
                      <span className="text-xl">{flag}</span>
                      <div>
                        <p className={`font-medium text-sm ${isSelected ? "text-[#FF6B00]" : "text-white"}`}>{name}</p>
                        <p className="text-xs text-[#666]">{country}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Teams grid */}
            <div>
              <h2 className="text-sm font-semibold text-[#FF6B00] uppercase tracking-wider mb-3">
                {filteredTeams.length > 0 ? `${filteredTeams.length} Clubs` : "All Clubs"}
              </h2>
              {filteredTeams.length > 0 ? (
                <div className="grid sm:grid-cols-2 gap-3">
                  {filteredTeams.map((team, i) => (
                    <Link
                      key={team.id || i}
                      to={`/clubs/${team.id}`}
                      className="bg-[#1A1A1A] rounded-xl border border-[#262626] p-4 flex items-center gap-4 hover:border-[#FF6B00] transition-colors group"
                    >
                      {team.logoUrl ? (
                        <img src={team.logoUrl} alt={team.name} className="w-10 h-10 object-contain" />
                      ) : (
                        <div className="w-10 h-10 rounded-lg bg-[#0F0F0F] grid place-items-center text-xl">🏟️</div>
                      )}
                      <div className="flex-1 min-w-0">
                        <p className="font-semibold text-white group-hover:text-[#FF6B00] transition-colors truncate">{team.name}</p>
                        <p className="text-xs text-[#B3B3B3]">{team.country}</p>
                      </div>
                      <span className="text-[#FF6B00] text-sm">→</span>
                    </Link>
                  ))}
                </div>
              ) : (
                <div className="bg-[#1A1A1A] rounded-xl border border-[#1A1A1A] p-8 text-center text-[#B3B3B3]">
                  <p className="text-2xl mb-2">🏟️</p>
                  <p>No clubs loaded yet.</p>
                  <p className="text-sm mt-1">Run a sync to populate club data.</p>
                </div>
              )}
            </div>
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default LeaguesPage;
