import React from "react";
import { Link } from "react-router-dom";

const Hero = () => {
  return (
    <section className="relative overflow-hidden border-b border-[#1A1A1A] bg-[#0F0F0F]">
      <div
        className="pointer-events-none absolute inset-0"
        style={{
          background:
            "radial-gradient(circle at 20% 20%, rgba(255,107,0,0.2), transparent 40%), radial-gradient(circle at 80% 65%, rgba(255,133,51,0.15), transparent 42%)",
        }}
      />

      <div className="relative mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8 py-12 sm:py-16 lg:py-20">
        <div className="grid gap-10 lg:grid-cols-[1.05fr_0.95fr] lg:items-center">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full bg-[#FF6B00] px-4 py-2 text-sm font-semibold text-[#FFFFFF] shadow-[0_0_24px_rgba(255,107,0,0.35)]">
              <span>↗</span>
              <span>Breaking Transfer News</span>
            </div>

            <h1 className="mt-6 text-3xl sm:text-4xl lg:text-6xl font-extrabold leading-[0.95] text-[#FFFFFF]">
              Your Ultimate
              <br />
              <span className="text-[#FF6B00]">Football Transfer</span>
              <br />
              Destination
            </h1>

            <p className="mt-6 max-w-2xl text-base sm:text-xl text-[#B3B3B3] leading-relaxed">
              Real-time transfer updates, player valuations, club statistics, and comprehensive football
              data at your fingertips.
            </p>

            <div className="mt-8 flex flex-col sm:flex-row gap-3 sm:gap-4">
              <Link
                to="/transfers"
                className="inline-flex items-center justify-center gap-2 rounded-xl bg-[#FF6B00] px-6 py-3 text-sm sm:text-base font-semibold text-[#FFFFFF] hover:bg-[#FF8533] transition-colors shadow-[0_0_24px_rgba(255,107,0,0.3)]"
              >
                Explore Transfers <span>→</span>
              </Link>
              <Link
                to="/players"
                className="inline-flex items-center justify-center rounded-xl border border-[#1A1A1A] bg-[#111111] px-6 py-3 text-sm sm:text-base font-semibold text-[#FFFFFF] hover:border-[#FF6B00] hover:text-[#FF8533] transition-colors"
              >
                View Top Players
              </Link>
            </div>

            <div className="mt-10 grid grid-cols-3 gap-4 sm:gap-8">
              <div>
                <p className="text-2xl sm:text-4xl font-extrabold text-[#FF6B00]">850K+</p>
                <p className="mt-1 text-xs sm:text-lg text-[#B3B3B3]">Players Tracked</p>
              </div>
              <div>
                <p className="text-2xl sm:text-4xl font-extrabold text-[#FF6B00]">62K+</p>
                <p className="mt-1 text-xs sm:text-lg text-[#B3B3B3]">Clubs Worldwide</p>
              </div>
              <div>
                <p className="text-2xl sm:text-4xl font-extrabold text-[#FF6B00]">€15B+</p>
                <p className="mt-1 text-xs sm:text-lg text-[#B3B3B3]">Transfer Value</p>
              </div>
            </div>
          </div>

          <article className="rounded-3xl border border-[#1A1A1A] bg-[#121212]/90 p-5 sm:p-8 shadow-[0_0_34px_rgba(255,107,0,0.2)]">
            <div className="flex items-center gap-3 text-[#B3B3B3]">
              <span className="rounded-full border border-[#FF6B00] bg-[#2a1205] px-3 py-1 text-sm font-bold text-[#FF6B00]">
                HOT
              </span>
              <span className="text-base">◷ 2 hours ago</span>
            </div>

            <h2 className="mt-4 text-2xl sm:text-4xl font-bold text-[#FFFFFF]">Mbappe to Real Madrid</h2>

            <div className="mt-6 flex items-center justify-between gap-4">
              <div className="flex items-center gap-4">
                <div className="h-14 w-14 sm:h-20 sm:w-20 rounded-full bg-[#1A1A1A] grid place-items-center text-2xl">
                  ⚽
                </div>
                <div>
                  <p className="text-xl sm:text-3xl font-semibold text-[#FFFFFF]">Kylian Mbappe</p>
                  <p className="text-base sm:text-xl text-[#B3B3B3]">Forward • France</p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-2xl sm:text-4xl font-extrabold text-[#FF6B00]">€180M</p>
                <p className="text-base sm:text-xl text-[#B3B3B3]">Market Value</p>
              </div>
            </div>

            <div className="mt-6 grid grid-cols-[1fr_auto_1fr_auto] items-center gap-3 sm:gap-6 rounded-2xl bg-[#161616] p-4 sm:p-6">
              <div>
                <div className="inline-flex rounded-xl bg-[#0F0F0F] px-4 py-2 text-base sm:text-2xl font-bold text-[#FFFFFF]">
                  PSG
                </div>
                <p className="mt-2 text-sm sm:text-xl text-[#B3B3B3]">From</p>
              </div>
              <div className="text-2xl sm:text-4xl font-bold text-[#FF6B00]">→</div>
              <div>
                <div className="inline-flex rounded-xl bg-[#0F0F0F] px-4 py-2 text-base sm:text-2xl font-bold text-[#FFFFFF]">
                  RMA
                </div>
                <p className="mt-2 text-sm sm:text-xl text-[#B3B3B3]">To</p>
              </div>
              <div className="border-l border-[#1A1A1A] pl-3 sm:pl-6">
                <p className="text-2xl sm:text-4xl font-extrabold text-[#FF6B00]">FREE</p>
                <p className="text-sm sm:text-xl text-[#B3B3B3]">Transfer Fee</p>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>
  );
};

export default Hero;
