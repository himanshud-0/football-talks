import React from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const leagues = [
  { name: "Premier League", country: "England", clubs: 20 },
  { name: "La Liga", country: "Spain", clubs: 20 },
  { name: "Serie A", country: "Italy", clubs: 20 },
  { name: "Bundesliga", country: "Germany", clubs: 18 },
  { name: "Ligue 1", country: "France", clubs: 18 },
];

const LeaguesPage = () => {
  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="px-4 sm:px-6 py-8 sm:py-10 max-w-5xl mx-auto flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-[#FFFFFF] mb-8">Top Leagues</h1>
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
              {leagues.map((league, index) => (
                <tr
                  key={league.name}
                  className="home-card border-t border-[#0F0F0F] hover:bg-[#262626] text-[#B3B3B3]"
                  style={{ animationDelay: `${index * 90}ms` }}
                >
                  <td className="px-6 py-4 font-medium text-[#FFFFFF]">{league.name}</td>
                  <td className="px-6 py-4">{league.country}</td>
                  <td className="px-6 py-4">{league.clubs}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default LeaguesPage;
