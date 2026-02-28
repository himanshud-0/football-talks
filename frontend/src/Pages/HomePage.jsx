import React from "react";
import { Link } from "react-router-dom";
import Header from "../Components/Header";
import Hero from "../Components/Hero";
import LatestNews from "../Components/LatestNews";
import TrendPlayers from "../Components/TrendPlayers";
import TransferTable from "../Components/TransferTable";
import Footer from "../Components/Footer";
import { playerSchemas } from "../data/playerSchemas";

const HomePage = () => {
  return (
    <div className="min-h-screen flex flex-col bg-[#1a1717]">
      <Header />
      <main className="flex-1">
        <Hero />

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

        <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 py-10">
          <div className="w-full flex items-center justify-between gap-4 mb-6">
            <h1 className="text-2xl font-bold text-[#FFFFFF]">Trending Players</h1>
            <Link to="/players" className="text-base sm:text-lg text-[#FF6B00] hover:text-[#FF8533]">
              View all
            </Link>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4 justify-items-center">
            {playerSchemas.slice(0, 8).map((player, index) => (
              <TrendPlayers
                key={`${player.name}-${index}`}
                title={player.name}
                news={player.position}
                description={player.description}
                src={player.image}
                imagePosition={player.imagePosition || player.image_position}
                club={player.club}
                age={player.age}
                nationality={player.nationality}
                appearances={player.appearances}
                goalsScored={player.goalsScored}
                assists={player.assists}
                rating={player.rating}
                specialAbility={player.specialAbility}
                delay={`${120 + index * 100}ms`}
              />
            ))}
          </div>
        </section>

        <TransferTable />
      </main>
      <Footer />
    </div>
  );
};

export default HomePage;
