import React, { useState, useEffect } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";

const ROUTE_MAP = {
  home: "/", news: "/news", players: "/players", player: "/players",
  transfers: "/transfers", transfer: "/transfers", leagues: "/leagues",
  league: "/leagues", clubs: "/leagues", club: "/leagues",
  standings: "/standings", standing: "/standings",
  fixtures: "/fixtures", fixture: "/fixtures", results: "/fixtures",
  login: "/login",
};

const resolveRoute = (value) => {
  const query = value.trim().toLowerCase();
  if (!query) return null;
  if (query.startsWith("/")) return query;
  if (ROUTE_MAP[query]) return ROUTE_MAP[query];
  const partialMatch = Object.entries(ROUTE_MAP).find(([key]) => query.includes(key));
  return partialMatch ? partialMatch[1] : null;
};

const buildPlayerSearchRoute = (value) => {
  const query = value.trim();
  if (!query) return null;
  return `/players?${new URLSearchParams({ search: query }).toString()}`;
};

const Header = () => {
  const navigate = useNavigate();
  const [searchValue, setSearchValue] = useState("");
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    const loadUser = () => {
      try {
        const stored = localStorage.getItem("user");
        setUser(stored ? JSON.parse(stored) : null);
      } catch { setUser(null); }
    };
    loadUser();
    window.addEventListener("storage", loadUser);
    const interval = setInterval(loadUser, 1000);
    return () => { window.removeEventListener("storage", loadUser); clearInterval(interval); };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
    setIsMenuOpen(false);
    navigate("/");
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const targetRoute = resolveRoute(searchValue) || buildPlayerSearchRoute(searchValue);
    if (!targetRoute) return;
    navigate(targetRoute);
    setSearchValue("");
    setIsMenuOpen(false);
  };

  const navLinkClass = ({ isActive }) =>
    `inline-flex items-center gap-1 lg:gap-2 px-2 py-1 rounded-md transition-colors whitespace-nowrap text-sm lg:text-base ${
      isActive ? "text-[#FF6B00]" : "text-[#B3B3B3] hover:text-[#FF8533]"
    }`;

  const navItems = [
    { to: "/", label: "Home", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><path d="M3 10.5L12 3l9 7.5"/><path d="M5 9.5V20h14V9.5"/><path d="M10 20v-6h4v6"/></svg> },
    { to: "/transfers", label: "Transfers", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><path d="M7 7h10"/><path d="M13 3l4 4-4 4"/><path d="M17 17H7"/><path d="M11 13l-4 4 4 4"/></svg> },
    { to: "/players", label: "Players", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><circle cx="8" cy="8" r="3"/><circle cx="16" cy="9" r="2.5"/><path d="M3.5 19c.7-2.6 2.6-4 4.5-4s3.8 1.4 4.5 4"/><path d="M12.5 19c.5-1.8 1.8-2.8 3.5-2.8 1.6 0 2.9 1 3.4 2.8"/></svg> },
    { to: "/leagues", label: "Clubs", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><path d="M12 3l3 3h4v5c0 4.5-3.3 7.7-7 10-3.7-2.3-7-5.5-7-10V6h4l3-3z"/></svg> },
    { to: "/standings", label: "Standings", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><path d="M3 3h18v4H3z"/><path d="M3 11h18v4H3z"/><path d="M3 19h18v4H3z"/></svg> },
    { to: "/fixtures", label: "Fixtures", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg> },
    { to: "/news", label: "News", icon: <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 8h10M7 12h6M7 16h10"/></svg> },
  ];

  const AuthButton = ({ mobile = false }) => {
    if (user) {
      return (
        <div className={`flex items-center gap-2 ${mobile ? "flex-col sm:flex-row" : ""}`}>
          <span className="text-[#B3B3B3] font-medium truncate max-w-[120px] text-sm">
            👤 {user.username || user.fullName || "User"}
          </span>
          <button
            onClick={handleLogout}
            className={`whitespace-nowrap bg-transparent border border-[#FF6B00] text-[#FF6B00] hover:bg-[#FF6B00] hover:text-white transition-colors font-medium rounded-xl ${
              mobile ? "px-4 py-2 text-sm w-full sm:w-auto" : "px-3 lg:px-4 py-2 text-sm"
            }`}
          >
            Logout
          </button>
        </div>
      );
    }
    return (
      <Link
        to="/login"
        onClick={() => setIsMenuOpen(false)}
        className={`inline-flex justify-center whitespace-nowrap bg-[#FF6B00] text-white hover:bg-[#FF8533] cursor-pointer font-medium rounded-xl transition-colors ${
          mobile ? "px-4 py-2 text-sm w-full sm:w-auto" : "px-3 lg:px-5 py-2 text-sm lg:text-base"
        }`}
      >
        Sign In
      </Link>
    );
  };

  return (
    <nav className="bg-gradient-to-r from-[#0F0F0F] to-[#121212] text-white shadow-md sticky top-0 z-50 border-b border-[#1A1A1A]">
      <div className="w-full px-3 sm:px-4 lg:px-6 py-3">

        {/* Mobile top bar */}
        <div className="flex items-center justify-between gap-3 lg:hidden">
          <Link to="/" className="flex items-center gap-2">
            <span className="h-9 w-9 rounded-xl bg-[#FF6B00] text-white text-xl font-bold grid place-items-center">FI</span>
            <span className="text-lg font-bold leading-none">Football<span className="text-[#FF6B00] hidden sm:inline">Insider</span></span>
          </Link>
          <button
            type="button"
            className="rounded border border-[#FF6B00] px-3 py-1 text-sm font-medium text-[#FF6B00]"
            onClick={() => setIsMenuOpen((prev) => !prev)}
          >
            Menu
          </button>
        </div>

        {/* Desktop nav */}
        <div className="hidden lg:grid lg:grid-cols-[auto_1fr_auto] lg:items-center lg:gap-3">
          <Link to="/" className="flex items-center gap-2">
            <span className="h-10 w-10 rounded-xl bg-[#FF6B00] text-white text-2xl font-bold grid place-items-center">FI</span>
            <span className="text-3xl font-bold leading-none tracking-tight">Football<span className="text-[#FF6B00]">Insider</span></span>
          </Link>

          <ul className="flex items-center justify-center gap-0.5 xl:gap-2 font-medium">
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink to={item.to} className={navLinkClass}>
                  {item.icon}
                  <span className="hidden xl:inline">{item.label}</span>
                </NavLink>
              </li>
            ))}
          </ul>

          <div className="flex items-center gap-2 justify-self-end">
            <form onSubmit={handleSubmit}>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[#B3B3B3]">
                  <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current" strokeWidth="2"><circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/></svg>
                </span>
                <input
                  type="text"
                  value={searchValue}
                  onChange={(e) => setSearchValue(e.target.value)}
                  placeholder="Search..."
                  className="w-[140px] lg:w-[200px] border border-[#1A1A1A] rounded-xl pl-9 pr-3 py-2 focus:outline-none focus:border-[#FF6B00] bg-[#121821] text-white placeholder:text-[#B3B3B3] text-sm"
                />
              </div>
            </form>
            <AuthButton />
          </div>
        </div>

        {/* Mobile menu */}
        <div className={`${isMenuOpen ? "flex" : "hidden"} lg:hidden flex-col gap-4 pt-4`}>
          <ul className="flex flex-col gap-2 font-medium">
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
              <input
                type="text"
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                placeholder="Search players, clubs..."
                className="w-full sm:w-64 border border-[#1A1A1A] rounded-lg px-3 py-2 focus:outline-none focus:border-[#FF6B00] bg-[#121821] text-white placeholder:text-[#B3B3B3]"
              />
            </form>
            <AuthButton mobile />
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;
