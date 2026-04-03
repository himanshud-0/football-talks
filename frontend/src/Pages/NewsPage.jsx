import React, { useEffect, useState } from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "https://football-talks.onrender.com";

const FALLBACK_NEWS = [
  {
    id: 1,
    title: "Champions League Knockout Update",
    summary: "Top clubs prepare for high-stakes fixtures this week.",
    category: "Match Update",
    content:
      "The Champions League knockout stage is heating up with top clubs from across Europe preparing for decisive fixtures. Teams are finalizing tactics and injury lists ahead of the crucial second legs.",
  },
  {
    id: 2,
    title: "Transfer Window Rumors Heat Up",
    summary: "Several clubs are linked with major attacking signings.",
    category: "Player Transfers",
    content:
      "The summer transfer window is drawing closer and speculation is intensifying. Several top clubs are reported to be tracking elite attacking talent, with fees expected to break records.",
  },
  {
    id: 3,
    title: "Manager Press Conference Highlights",
    summary: "Tactical changes and injury updates ahead of weekend games.",
    category: "Latest News",
    content:
      "Multiple managers held press conferences this week, addressing squad injuries, formation changes, and upcoming fixture challenges. Key players remain doubtful for the weekend fixtures.",
  },
];

const CategoryBadge = ({ category }) => (
  <span className="text-xs font-semibold text-[#FF6B00] uppercase tracking-wider">
    {category}
  </span>
);

const NewsCard = ({ item }) => {
  const [expanded, setExpanded] = useState(false);

  const title = item.title || item.headline || "Untitled";
  const summary = item.summary || item.description || item.body?.slice(0, 120) + "..." || "";
  const content = item.content || item.body || item.fullContent || summary;
  const category = item.category || item.type || item.tag || "Latest News";
  const date = item.createdAt || item.publishedAt || item.date;

  return (
    <article className="bg-[#1A1A1A] rounded-xl shadow-md p-6 border border-[#0F0F0F] flex flex-col gap-3">
      <CategoryBadge category={category} />
      <h2 className="text-xl font-semibold text-[#FFFFFF] leading-snug">{title}</h2>
      <p className="text-[#B3B3B3] text-sm leading-relaxed">
        {expanded ? content : summary}
      </p>
      {date && (
        <p className="text-xs text-[#555]">
          {new Date(date).toLocaleDateString("en-GB", {
            day: "numeric",
            month: "short",
            year: "numeric",
          })}
        </p>
      )}
      <button
        onClick={() => setExpanded((prev) => !prev)}
        className="mt-auto self-start text-sm font-semibold text-[#FF6B00] hover:text-[#FF8533] transition-colors"
      >
        {expanded ? "Show less ↑" : "See more →"}
      </button>
    </article>
  );
};

const NewsPage = () => {
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${API_BASE}/api/posts`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data)
          ? data
          : data?.content || data?.data || data?.posts || [];
        setNews(list.length > 0 ? list : FALLBACK_NEWS);
        setLoading(false);
      })
      .catch(() => {
        setNews(FALLBACK_NEWS);
        setLoading(false);
      });
  }, []);

  return (
    <div className="min-h-screen bg-[#0F0F0F] flex flex-col">
      <Header />
      <main className="px-4 sm:px-6 py-8 sm:py-10 max-w-6xl mx-auto flex-1 w-full">
        <h1 className="text-3xl sm:text-4xl font-bold text-[#FFFFFF] mb-2">Football News</h1>
        <p className="text-[#B3B3B3] mb-8">
          {loading ? "Loading..." : `${news.length} articles available.`}
        </p>

        {loading ? (
          <div className="text-center py-16 text-[#B3B3B3]">
            <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
            <p>Loading news...</p>
          </div>
        ) : (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {news.map((item, index) => (
              <NewsCard key={item.id || index} item={item} />
            ))}
          </div>
        )}
      </main>
      <Footer />
    </div>
  );
};

export default NewsPage;
