import React, { useEffect, useState } from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const LEAGUES = [
  { id: 39, name: "Premier League", flag: "🏴󠁧󠁢󠁥󠁮󠁧󠁿" },
  { id: 140, name: "La Liga", flag: "🇪🇸" },
  { id: 135, name: "Serie A", flag: "🇮🇹" },
  { id: 78, name: "Bundesliga", flag: "🇩🇪" },
  { id: 61, name: "Ligue 1", flag: "🇫🇷" },
  { id: 2, name: "Champions League", flag: "🏆" },
];

const StatusBadge = ({ status }) => {
  const isLive = ["1H", "2H", "HT", "ET", "BT", "P", "LIVE"].includes(status);
  const isDone = ["FT", "AET", "PEN"].includes(status);
  if (isLive) return <span className="text-xs bg-red-600 text-white px-2 py-0.5 rounded-full font-bold animate-pulse">LIVE</span>;
  if (isDone) return <span className="text-xs bg-[#333] text-[#B3B3B3] px-2 py-0.5 rounded-full">FT</span>;
  return <span className="text-xs bg-[#1A1A1A] text-[#B3B3B3] px-2 py-0.5 rounded-full border border-[#333]">Upcoming</span>;
};

const formatDate = (dateStr) => {
  if (!dateStr) return "—";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-GB", { weekday: "short", day: "numeric", month: "short" })
    + " · " + d.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit" });
};

const FixtureCard = ({ fixture }) => {
  const isPlayed = fixture.homeGoals !== null && fixture.awayGoals !== null;
  const homeWin = fixture.homeTeam?.winner;
  const awayWin = fixture.awayTeam?.winner;

  return (
    <div className="bg-[#1A1A1A] rounded-xl border border-[#262626] p-4 hover:border-[#FF6B00] transition-colors">
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs text-[#666]">{formatDate(fixture.date)}</span>
        <StatusBadge status={fixture.status} />
      </div>

      <div className="flex items-center justify-between gap-2">
        {/* Home */}
        <div className="flex-1 flex items-center gap-3">
          {fixture.homeTeam?.logo && (
            <img src={fixture.homeTeam.logo} alt={fixture.homeTeam.name} className="w-8 h-8 object-contain" />
          )}
          <span className={`font-semibold text-sm ${homeWin ? "text-white" : "text-[#B3B3B3]"}`}>
            {fixture.homeTeam?.name}
          </span>
        </div>

        {/* Score */}
        <div className="flex items-center gap-2 px-3">
          {isPlayed ? (
            <span className="text-xl font-extrabold text-white tabular-nums">
              {fixture.homeGoals} – {fixture.awayGoals}
            </span>
          ) : (
            <span className="text-sm font-bold text-[#FF6B00]">vs</span>
          )}
        </div>

        {/* Away */}
        <div className="flex-1 flex items-center gap-3 justify-end">
          <span className={`font-semibold text-sm ${awayWin ? "text-white" : "text-[#B3B3B3]"}`}>
            {fixture.awayTeam?.name}
          </span>
          {fixture.awayTeam?.logo && (
            <img src={fixture.awayTeam.logo} alt={fixture.awayTeam.name} className="w-8 h-8 object-contain" />
          )}
        </div>
      </div>

      {fixture.venue && (
        <p className="text-xs text-[#555] mt-2 text-center">📍 {fixture.venue}</p>
      )}
    </div>
  );
};

const FixturesPage = () => {
  const [selectedLeague, setSelectedLeague] = useState(LEAGUES[0]);
  const [type, setType] = useState("next");
  const [fixtures, setFixtures] = useState([]);
  const [loading, setLoading] = useState(true);
  const [empty, setEmpty] = useState(false);

  useEffect(() => {
    setLoading(true);
    setEmpty(false);
    fetch(`${API_BASE}/api/fixtures?league=${selectedLeague.id}&type=${type}`)
      .then((r) => r.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : [];
        setFixtures(list);
        setEmpty(list.length === 0);
        setLoading(false);
      })
      .catch(() => {
        setEmpty(true);
        setLoading(false);
      });
  }, [selectedLeague, type]);

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="max-w-4xl mx-auto px-4 sm:px-6 py-8 flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-white mb-2">Fixtures & Results</h1>
        <p className="text-[#B3B3B3] mb-6">Upcoming matches and recent results.</p>

        {/* League tabs */}
        <div className="flex flex-wrap gap-2 mb-4">
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

        {/* Type toggle */}
        <div className="flex gap-2 mb-6">
          {[
            { key: "next", label: "Upcoming" },
            { key: "last", label: "Results" },
            { key: "live", label: "🔴 Live" },
          ].map((t) => (
            <button
              key={t.key}
              onClick={() => setType(t.key)}
              className={`px-4 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                type === t.key
                  ? "bg-white text-black"
                  : "bg-[#1A1A1A] text-[#B3B3B3] hover:bg-[#262626]"
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>

        {loading && (
          <div className="text-center py-20 text-[#B3B3B3]">
            <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
            <p>Loading fixtures...</p>
          </div>
        )}

        {empty && !loading && (
          <div className="text-center py-20 text-[#B3B3B3] bg-[#1A1A1A] rounded-2xl border border-[#262626]">
            <p className="text-3xl mb-3">⚽</p>
            <p className="text-lg font-semibold text-white mb-1">No fixtures found</p>
            <p className="text-sm">API key may not be configured or no matches scheduled.</p>
          </div>
        )}

        {!loading && fixtures.length > 0 && (
          <div className="flex flex-col gap-3">
            {fixtures.map((f, i) => (
              <FixtureCard key={f.fixtureId || i} fixture={f} />
            ))}
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default FixturesPage;
