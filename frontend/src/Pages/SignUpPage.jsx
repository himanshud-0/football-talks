import React from "react";
import { Link } from "react-router-dom";
import Header from "../Components/Header";

const SignUpPage = () => {
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
              id="fullName"
              placeholder="Full Name"
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "120ms" }}
            />
            <input
              type="email"
              name="email"
              id="email"
              placeholder="Email"
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "200ms" }}
            />
            <input
              type="password"
              name="password"
              id="password"
              placeholder="Password"
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "280ms" }}
            />
            <input
              type="password"
              name="confirmPassword"
              id="confirmPassword"
              placeholder="Confirm Password"
              className="login-stagger block w-full p-2 border border-[#0F0F0F] bg-[#0F0F0F] text-[#FFFFFF] placeholder:text-[#B3B3B3] rounded-md focus:outline-none focus:ring-2 focus:ring-[#FF6B00]"
              style={{ animationDelay: "360ms" }}
            />
            <button
              className="login-stagger block w-full p-2 bg-[#FF6B00] text-[#FFFFFF] rounded-md hover:bg-[#FF8533] transition-colors duration-300"
              style={{ animationDelay: "440ms" }}
            >
              Create Account
            </button>
            <p
              className="login-stagger text-center text-sm text-[#B3B3B3]"
              style={{ animationDelay: "520ms" }}
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
