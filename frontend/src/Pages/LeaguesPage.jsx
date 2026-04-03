import React, { useEffect, useState } from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "https://football-talks.onrender.com";

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
  const [leagues, setLeagues] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    fetch(`${API_BASE}/api/competitions`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || data?.data || [];
        setLeagues(list);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  }, []);

  // Fallback static data if API returns nothing
  const fallbackLeagues = [
    { name: "Premier League", country: "England", clubs: 20 },
    { name: "La Liga", country: "Spain", clubs: 20 },
    { name: "Serie A", country: "Italy", clubs: 20 },
    { name: "Bundesliga", country: "Germany", clubs: 18 },
    { name: "Ligue 1", country: "France", clubs: 18 },
  ];

  const displayLeagues = leagues.length > 0 ? leagues : fallbackLeagues;

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="px-4 sm:px-6 py-8 sm:py-10 max-w-5xl mx-auto flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-[#FFFFFF] mb-2">Top Leagues</h1>
        <p className="text-[#B3B3B3] mb-8">
          {loading ? "Loading..." : `${displayLeagues.length} competitions tracked.`}
        </p>

        {loading && (
          <div className="text-center py-16 text-[#B3B3B3]">
            <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
            <p>Loading leagues...</p>
          </div>
        )}

        {!loading && (
          <div className="overflow-x-auto rounded-xl shadow-md border border-[#1A1A1A] bg-[#1A1A1A]">
            <table className="w-full min-w-[560px]">
              <thead className="bg-[#0F0F0F]">
                <tr className="text-left text-[#FF6B00]">
                  <th className="px-6 py-3">League</th>
                  <th className="px-6 py-3">Country</th>
                  <th className="px-6 py-3">Clubs</th>
                </tr>
              </thead>
              <tbody>
                {displayLeagues.map((league, index) => {
                  const name = league.name || league.leagueName;
                  const country = league.country || league.nation || "—";
                  const clubs = league.clubs || league.teamCount || league.numberOfTeams || "—";
                  const flag = LEAGUE_FLAGS[name] || "🌍";

                  return (
                    <tr
                      key={league.id || league.competitionId || index}
                      className="border-t border-[#0F0F0F] hover:bg-[#262626] text-[#B3B3B3] transition-colors"
                    >
                      <td className="px-6 py-4 font-medium text-[#FFFFFF]">
                        <span className="mr-2">{flag}</span>{name}
                      </td>
                      <td className="px-6 py-4">{country}</td>
                      <td className="px-6 py-4">{clubs}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

        {error && !loading && (
          <p className="text-center text-[#B3B3B3] mt-4 text-sm">
            Showing cached data — live update failed.
          </p>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default LeaguesPage;
