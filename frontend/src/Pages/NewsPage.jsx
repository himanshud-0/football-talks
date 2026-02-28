import React from "react";
import { Link } from "react-router-dom";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const newsItems = [
  {
    title: "Champions League Knockout Update",
    summary: "Top clubs prepare for high-stakes fixtures this week.",
  },
  {
    title: "Transfer Window Rumors Heat Up",
    summary: "Several clubs are linked with major attacking signings.",
  },
  {
    title: "Manager Press Conference Highlights",
    summary: "Tactical changes and injury updates ahead of weekend games.",
  },
];

const NewsPage = () => {
  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="px-4 sm:px-6 py-8 sm:py-10 max-w-6xl mx-auto flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-[#FFFFFF] mb-8">Football News</h1>
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {newsItems.map((item, index) => (
            <article
              key={item.title}
              className="home-card bg-[#1A1A1A] rounded-xl shadow-md p-6 border border-[#0F0F0F]"
              style={{ animationDelay: `${index * 120}ms` }}
            >
              <h2 className="text-xl font-semibold text-[#FFFFFF]">{item.title}</h2>
              <p className="text-[#B3B3B3] mt-3">{item.summary}</p>
              <Link
                to="/news"
                className="mt-4 inline-block text-sm font-semibold text-[#FF6B00] hover:text-[#FF8533]"
              >
                See more
              </Link>
            </article>
          ))}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default NewsPage;
