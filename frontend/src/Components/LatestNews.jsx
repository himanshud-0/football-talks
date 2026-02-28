import React from "react";
import { Link } from "react-router-dom";

const LatestNews = ({ title, news, description, src, delay = "0ms", seeMoreTo = "/news" }) => {
  return (
    <div
      className="home-card w-full max-w-sm rounded-lg shadow-lg overflow-hidden hover:scale-[1.02] transition-transform duration-300 ease-in-out bg-[#1A1A1A]"
      style={{ animationDelay: delay }}
    >
      <img src={src} alt={news} className="w-full h-44 object-cover" />
      <div className="p-4">
        <h3 className="text-[#FF6B00]">{title}</h3>
        <h2 className="text-lg font-semibold text-[#FFFFFF]">{news}</h2>
        <p className="text-[#B3B3B3] mt-2">{description}</p>
        <Link
          to={seeMoreTo}
          className="mt-4 inline-block text-sm font-semibold text-[#FF6B00] hover:text-[#FF8533]"
        >
          See more
        </Link>
      </div>
    </div>
  );
};

export default LatestNews;
