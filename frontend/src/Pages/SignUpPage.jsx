import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../Components/Header";

const API_BASE = import.meta.env.VITE_API_BASE || "https://football-talks.onrender.com";

const SignUpPage = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: "",
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError("");
  };

  const handleSignUp = async () => {
    const { fullName, email, username, password, confirmPassword } = form;

    if (!fullName.trim() || !email.trim() || !username.trim() || !password || !confirmPassword) {
      setError("Please fill in all fields.");
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await fetch(`${API_BASE}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          fullName: fullName.trim(),
          email: email.trim(),
          username: username.trim(),
          password,
        }),
      });

      const data = await res.json();

      if (!res.ok) {
        setError(data.message || "Registration failed. Please try again.");
        return;
      }

      setSuccess("Account created! Redirecting to login...");
      setTimeout(() => navigate("/login"), 1500);
    } catch {
      setError("Could not connect to server. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSignUp();
  };

  return (
    <div className="login-page-bg min-h-screen flex flex-col">
      <Header />
      <main className="flex-1 flex justify-center items-center p-4 sm:p-6">
        <div className="login-card w-full max-w-sm rounded-2xl bg-[#1A1A1A] p-6 sm:p-8 border border-[#FF6B00]">
          <h1 className="login-stagger text-2xl sm:text-3xl font-bold text-center text-[#FF6B00] mb-6">
            Sign Up
          </h1>

          <div className="space-y-4">
            <input
              type="text"
              name="fullName"
              placeholder="Full Name"
              value={form.fullName}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "80ms" }}
            />
            <input
              type="text"
              name="username"
              placeholder="Username"
              value={form.username}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "160ms" }}
            />
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={form.email}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "240ms" }}
            />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={form.password}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "320ms" }}
            />
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              value={form.confirmPassword}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "400ms" }}
            />

            {error && (
              <p className="text-sm text-red-400 text-center">{error}</p>
            )}
            {success && (
              <p className="text-sm text-green-400 text-center">{success}</p>
            )}

            <button
              onClick={handleSignUp}
              disabled={loading}
              className="login-stagger block w-full p-2 bg-[#FF6B00] text-[#FFFFFF] rounded-md hover:bg-[#FF8533] transition-colors duration-300 disabled:opacity-60 disabled:cursor-not-allowed"
              style={{ animationDelay: "480ms" }}
            >
              {loading ? "Creating account..." : "Create Account"}
            </button>

            <p
              className="login-stagger text-center text-sm text-[#B3B3B3]"
              style={{ animationDelay: "540ms" }}
            >
              Already have an account?{" "}
              <Link to="/login" className="font-semibold text-[#FF6B00] hover:text-[#FF8533] hover:underline">
                Login
              </Link>
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};

export default SignUpPage;
