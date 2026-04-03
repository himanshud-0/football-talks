import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../Components/Header";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const LoginPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleLogin = async () => {
    if (!form.username.trim() || !form.password.trim()) {
      setError("Please enter both username and password.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await fetch(`${API_BASE}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: form.username.trim(),
          password: form.password,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        setError(data.message || "Invalid username or password.");
        return;
      }

      // Save token and user info
      localStorage.setItem("token", data.token || data.accessToken || "");
      localStorage.setItem("user", JSON.stringify(data.user || { username: form.username }));

      navigate("/");
    } catch {
      setError("Could not connect to server. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleLogin();
  };

  return (
    <div className="login-page-bg min-h-screen flex flex-col">
      <Header />
      <main className="flex-1 flex justify-center items-center p-4 sm:p-6">
        <div className="login-card w-full max-w-sm rounded-2xl bg-[#1A1A1A] p-6 sm:p-8 border border-[#FF6B00]">
          <h1 className="login-stagger text-2xl sm:text-3xl font-bold text-center text-[#FF6B00] mb-6">
            Login
          </h1>

          <div className="space-y-4">
            <input
              type="text"
              name="username"
              id="username"
              placeholder="Username"
              value={form.username}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "120ms" }}
            />
            <input
              type="password"
              name="password"
              id="password"
              placeholder="Password"
              value={form.password}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "220ms" }}
            />

            {error && (
              <p className="text-sm text-red-400 text-center">{error}</p>
            )}

            <button
              onClick={handleLogin}
              disabled={loading}
              className="login-stagger block w-full p-2 bg-[#FF6B00] text-[#FFFFFF] rounded-md hover:bg-[#FF8533] transition-colors duration-300 disabled:opacity-60 disabled:cursor-not-allowed"
              style={{ animationDelay: "320ms" }}
            >
              {loading ? "Logging in..." : "Login"}
            </button>

            <p
              className="login-stagger text-center text-sm text-[#B3B3B3]"
              style={{ animationDelay: "380ms" }}
            >
              Don&apos;t have an account?{" "}
              <Link to="/signup" className="font-semibold text-[#FF6B00] hover:text-[#FF8533] hover:underline">
                Sign up
              </Link>
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};

export default LoginPage;
