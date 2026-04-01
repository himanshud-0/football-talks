import React, { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";

const ROUTE_MAP = {
  home: "/",
  news: "/news",
  players: "/players",
  player: "/players",
  transfers: "/transfers",
  transfer: "/transfers",
  leagues: "/leagues",
  league: "/leagues",
  clubs: "/leagues",
  club: "/leagues",
  login: "/login",
};

const resolveRoute = (value) => {
  const query = value.trim().toLowerCase();

  if (!query) return null;
  if (query.startsWith("/")) return query;
  if (ROUTE_MAP[query]) return ROUTE_MAP[query];

  const partialMatch = Object.entries(ROUTE_MAP).find(([key]) =>
    query.includes(key)
  );

  return partialMatch ? partialMatch[1] : null;
};

const buildPlayerSearchRoute = (value) => {
  const query = value.trim();
  if (!query) return null;

  const params = new URLSearchParams({ search: query });
  return `/players?${params.toString()}`;
};

const Header = () => {
  const navigate = useNavigate();
  const [searchValue, setSearchValue] = useState("");
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleSubmit = (event) => {
    event.preventDefault();

    const targetRoute = resolveRoute(searchValue) || buildPlayerSearchRoute(searchValue);
    if (!targetRoute) return;

    navigate(targetRoute);
    setSearchValue("");
    setIsMenuOpen(false);
  };

  const navLinkClass = ({ isActive }) =>
    `inline-flex items-center gap-1 lg:gap-2 px-2 py-1 rounded-md transition-colors whitespace-nowrap ${
      isActive ? "text-[#FF6B00]" : "text-[#B3B3B3] hover:text-[#FF8533]"
    }`;

  const navItems = [
    {
      to: "/",
      label: "Home",
      icon: (
        <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2">
          <path d="M3 10.5L12 3l9 7.5" />
          <path d="M5 9.5V20h14V9.5" />
          <path d="M10 20v-6h4v6" />
        </svg>
      ),
    },
    {
      to: "/transfers",
      label: "Transfers",
      icon: (
        <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2">
          <path d="M7 7h10" />
          <path d="M13 3l4 4-4 4" />
          <path d="M17 17H7" />
          <path d="M11 13l-4 4 4 4" />
        </svg>
      ),
    },
    {
      to: "/players",
      label: "Players",
      icon: (
        <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2">
          <circle cx="8" cy="8" r="3" />
          <circle cx="16" cy="9" r="2.5" />
          <path d="M3.5 19c.7-2.6 2.6-4 4.5-4s3.8 1.4 4.5 4" />
          <path d="M12.5 19c.5-1.8 1.8-2.8 3.5-2.8 1.6 0 2.9 1 3.4 2.8" />
        </svg>
      ),
    },
    {
      to: "/leagues",
      label: "Clubs",
      icon: (
        <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2">
          <path d="M12 3l3 3h4v5c0 4.5-3.3 7.7-7 10-3.7-2.3-7-5.5-7-10V6h4l3-3z" />
          <path d="M12 7v7" />
          <path d="M8.5 10.5h7" />
        </svg>
      ),
    },
    {
      to: "/news",
      label: "News",
      icon: (
        <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2">
          <rect x="3" y="4" width="18" height="16" rx="2" />
          <path d="M7 8h10" />
          <path d="M7 12h6" />
          <path d="M7 16h10" />
        </svg>
      ),
    },
  ];

  return (
    <nav className="bg-gradient-to-r from-[#0F0F0F] to-[#121212] text-[#FFFFFF] shadow-md sticky top-0 z-50 border-b border-[#1A1A1A]">
      <div className="w-full px-3 sm:px-4 lg:px-6 py-3">
        <div className="flex items-center justify-between gap-3 lg:hidden">
          <Link to="/" className="flex items-center gap-2">
            <span className="h-9 w-9 rounded-xl bg-[#FF6B00] text-[#FFFFFF] text-xl font-bold grid place-items-center">
              FI
            </span>
            <span className="text-lg sm:text-2xl font-bold text-[#FFFFFF] leading-none">
              Football<span className="hidden sm:inline text-[#FF6B00]">Insider</span>
            </span>
          </Link>
          <button
            type="button"
            className="rounded border border-[#FF6B00] px-3 py-1 text-sm font-medium text-[#FF6B00]"
            onClick={() => setIsMenuOpen((prev) => !prev)}
            aria-expanded={isMenuOpen}
            aria-label="Toggle navigation menu"
          >
            Menu
          </button>
        </div>

        <div className="hidden lg:grid lg:grid-cols-[auto_1fr_auto] lg:items-center lg:gap-3 xl:gap-6">
          <Link to="/" className="flex items-center gap-2 justify-self-start">
            <span className="h-8 w-8 lg:h-10 lg:w-10 rounded-xl bg-[#FF6B00] text-[#FFFFFF] text-lg lg:text-2xl font-bold grid place-items-center">
              FI
            </span>
            <span className="text-xl lg:text-3xl xl:text-4xl font-bold leading-none tracking-tight text-[#FFFFFF]">
              Football<span className="text-[#FF6B00]">Insider</span>
            </span>
          </Link>
          <ul className="flex items-center justify-center gap-1 lg:gap-4 font-medium text-sm lg:text-lg xl:text-xl">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink to={item.to} className={navLinkClass}>
                  {item.icon}
                  <span className="hidden lg:inline">{item.label}</span>
                </NavLink>
              </li>
            ))}
          </ul>
          <div className="flex items-center gap-2 lg:gap-3 justify-self-end">
            <form onSubmit={handleSubmit} className="w-full sm:w-auto">
              <label htmlFor="header-search-desktop" className="sr-only">
                Search pages
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[#B3B3B3]">
                  <svg viewBox="0 0 24 24" className="h-5 w-5 fill-none stroke-current" strokeWidth="2">
                    <circle cx="11" cy="11" r="7" />
                    <path d="M20 20l-4-4" />
                  </svg>
                </span>
                <input
                  id="header-search-desktop"
                  type="text"
                  value={searchValue}
                  onChange={(event) => setSearchValue(event.target.value)}
                  placeholder="Search players, clubs..."
                  className="w-[180px] lg:w-[250px] xl:w-[330px] border border-[#1A1A1A] rounded-xl pl-10 pr-4 py-2 lg:py-2.5 focus:outline-none focus:border-[#FF6B00] bg-[#121821] text-[#FFFFFF] placeholder:text-[#B3B3B3]"
                />
              </div>
            </form>
            <Link
              to="/login"
              className="inline-flex justify-center whitespace-nowrap bg-[#FF6B00] text-[#FFFFFF] px-3 lg:px-5 py-2 lg:py-2.5 rounded-xl hover:bg-[#FF8533] cursor-pointer font-medium text-sm lg:text-base"
            >
              Sign In
            </Link>
          </div>
        </div>

        <div
          className={`${
            isMenuOpen ? "flex" : "hidden"
          } lg:hidden flex-col gap-4 pt-4`}
        >
          <ul className="flex flex-col gap-3 font-medium text-left">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink to={item.to} className={navLinkClass} onClick={() => setIsMenuOpen(false)}>
                  {item.icon}
                  <span>{item.label}</span>
                </NavLink>
              </li>
            ))}
          </ul>
          <div className="flex flex-col sm:flex-row sm:items-center gap-3">
            <form onSubmit={handleSubmit} className="w-full sm:w-auto">
              <label htmlFor="header-search-mobile" className="sr-only">
                Search pages
              </label>
              <input
                id="header-search-mobile"
                type="text"
                value={searchValue}
                onChange={(event) => setSearchValue(event.target.value)}
                placeholder="Search players, clubs..."
                className="w-full sm:w-72 border border-[#1A1A1A] rounded-lg px-3 py-2 focus:outline-none focus:border-[#FF6B00] bg-[#121821] text-[#FFFFFF] placeholder:text-[#B3B3B3]"
              />
            </form>
            <Link
              to="/login"
              onClick={() => setIsMenuOpen(false)}
              className="inline-flex justify-center whitespace-nowrap bg-[#FF6B00] text-[#FFFFFF] px-4 py-2 rounded-lg hover:bg-[#FF8533] cursor-pointer"
            >
              Sign In
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;
