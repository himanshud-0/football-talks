import React from "react";
import { Link } from "react-router-dom";

const TrendPlayers = ({
  title,
  news,
  description,
  src,
  imagePosition = "center 14%",
  club,
  age,
  nationality,
  appearances,
  goalsScored,
  assists,
  rating,
  specialAbility,
  delay = "0ms",
  seeMoreTo = "/players",
}) => {
  const resolvedImagePosition = imagePosition || "center 14%";
  const playerName = title || news || "Player";
  const resolvedSrc =
    typeof src === "string" && src.includes("/portrait/header/")
      ? src.replace("/portrait/header/", "/portrait/big/")
      : src;
  const fallbackSrc = `https://ui-avatars.com/api/?name=${encodeURIComponent(
    playerName
  )}&background=111827&color=ffffff&bold=true&size=512`;
  const hasStats =
    appearances !== undefined ||
    goalsScored !== undefined ||
    assists !== undefined ||
    rating !== undefined ||
    !!specialAbility;

  return (
    <div
      className="group home-card relative w-[90%] max-w-[260px] h-[350px] rounded-lg shadow-lg overflow-hidden hover:scale-[1.02] transition-transform duration-300 ease-in-out"
      style={{ animationDelay: delay }}
    >
      <img
        src={resolvedSrc || fallbackSrc}
        alt={title}
        className="absolute inset-0 w-full h-full object-cover"
        style={{ objectPosition: resolvedImagePosition, filter: "brightness(0.62)" }}
        onError={(event) => {
          const target = event.currentTarget;
          if (target.dataset.fallbackApplied === "true") return;
          target.dataset.fallbackApplied = "true";
          target.src = fallbackSrc;
        }}
      />
      <div className="absolute inset-0 bg-gradient-to-t from-[#0A0A0A]/78 via-[#0A0A0A]/28 to-transparent" />
      <div className="relative z-10 h-full p-3">
        <div className="absolute inset-x-3 bottom-1">
          <h3 className="relative z-20 text-sm text-[#FF6B00] transition-all duration-300 ease-out group-hover:opacity-0">
            {title}
          </h3>
          <div className="relative mt-1">
            <div className="transition-all duration-300 ease-out group-hover:opacity-0 group-hover:-translate-y-2">
              <h2 className="text-base font-semibold leading-tight text-[#FFFFFF]">{news}</h2>
              <p className="mt-1 text-xs leading-tight text-[#D1D5DB]">{description}</p>
              <div className="mt-1.5 flex flex-wrap gap-1 text-[10px]">
                <span className="px-2 py-0.5 bg-[#1A1A1A]/65 text-[#FFFFFF] rounded">{club}</span>
                <span className="px-2 py-0.5 bg-[#1A1A1A]/65 text-[#FFFFFF] rounded">Age: {age}</span>
                <span className="px-2 py-0.5 bg-[#1A1A1A]/65 text-[#FFFFFF] rounded">{nationality}</span>
              </div>
            </div>
            {hasStats ? (
              <div className="absolute inset-x-0 bottom-0 z-10 pointer-events-none">
                <h3 className="mb-2 max-w-full truncate whitespace-nowrap text-sm leading-none text-[#FF6B00] drop-shadow-[0_1px_2px_rgba(0,0,0,0.8)] opacity-0 transition-opacity duration-200 ease-out group-hover:opacity-100">
                  {title}
                </h3>
                <div className="rounded-lg bg-black/55 p-2.5 backdrop-blur-sm border border-white/15 opacity-0 translate-y-3 scale-95 transition-all duration-300 ease-out group-hover:opacity-100 group-hover:translate-y-0 group-hover:scale-100">
                  <p className="mb-1 text-[10px] uppercase tracking-wide text-[#FFB37D]">All-Time Stats</p>
                  <div className="grid grid-cols-4 gap-1 text-[10px]">
                    <div className="text-center">
                      <p className="text-[#9CA3AF]">Apps</p>
                      <p className="text-[#FFFFFF] font-semibold">{appearances ?? "-"}</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[#9CA3AF]">Goals</p>
                      <p className="text-[#FFFFFF] font-semibold">{goalsScored ?? "-"}</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[#9CA3AF]">Assists</p>
                      <p className="text-[#FFFFFF] font-semibold">{assists ?? "-"}</p>
                    </div>
                    <div className="text-center">
                      <p className="text-[#9CA3AF]">Rating</p>
                      <p className="text-[#FFFFFF] font-semibold">{rating ?? "-"}</p>
                    </div>
                  </div>
                  {specialAbility ? (
                    <p className="mt-1 text-[10px] text-[#FFB37D] leading-tight">
                      Ability: <span className="text-[#FFFFFF] break-words">{specialAbility}</span>
                    </p>
                  ) : null}
                </div>
              </div>
            ) : null}
          </div>
          <Link
            to={seeMoreTo}
            className="mt-1.5 inline-block text-xs font-semibold text-[#FF6B00] hover:text-[#FF8533]"
          >
            See more
          </Link>
        </div>
      </div>
    </div>
  );
};

export default TrendPlayers;
