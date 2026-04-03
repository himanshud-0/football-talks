import React, { useEffect, useState } from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "https://football-talks.onrender.com";

const TransferPage = () => {
  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    fetch(`${API_BASE}/api/transfers`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || data?.data || [];
        setTransfers(list);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  }, []);

  const formatFee = (fee) => {
    if (!fee || fee === 0) return "Free";
    if (fee >= 1_000_000) return `€${(fee / 1_000_000).toFixed(0)}M`;
    if (fee >= 1_000) return `€${(fee / 1_000).toFixed(0)}K`;
    return `€${fee}`;
  };

  return (
    <div className="transfer-page-bg min-h-screen flex flex-col">
      <Header />
      <main className="flex-1">
        <div className="px-4 sm:px-6 pt-10 sm:pt-12 pb-8">
          <h1 className="transfer-title text-3xl sm:text-4xl font-bold text-center text-[#FF6B00]">
            Transfer News
          </h1>
          <p className="transfer-subtitle text-center text-[#B3B3B3] mt-3">
            Latest completed and trending transfer moves.
          </p>
        </div>

        <div className="transfer-table-wrap px-4 sm:px-6 max-w-5xl mx-auto pb-12">
          <h2 className="text-xl font-bold text-white mb-4">Recent Transfers</h2>

          {loading && (
            <div className="text-center py-16 text-[#B3B3B3]">
              <div className="inline-block w-8 h-8 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-4"></div>
              <p>Loading transfers...</p>
            </div>
          )}

          {error && (
            <div className="text-center py-16 text-[#B3B3B3]">
              <p className="text-2xl mb-2">⚠️</p>
              <p>Could not load transfers. Please try again later.</p>
            </div>
          )}

          {!loading && !error && transfers.length === 0 && (
            <div className="text-center py-16 text-[#B3B3B3]">
              <p className="text-2xl mb-2">📋</p>
              <p>No transfer data available yet.</p>
            </div>
          )}

          {!loading && !error && transfers.length > 0 && (
            <div className="overflow-x-auto rounded-xl border border-[#1A1A1A] bg-[#1A1A1A]">
              <table className="w-full min-w-[560px]">
                <thead className="bg-[#0F0F0F]">
                  <tr className="text-left text-[#FF6B00]">
                    <th className="px-6 py-3">Player</th>
                    <th className="px-6 py-3">From Club</th>
                    <th className="px-6 py-3">To Club</th>
                    <th className="px-6 py-3">Transfer Fee</th>
                  </tr>
                </thead>
                <tbody>
                  {transfers.map((t, i) => (
                    <tr
                      key={t.id || i}
                      className="border-t border-[#0F0F0F] hover:bg-[#262626] text-[#B3B3B3] transition-colors"
                    >
                      <td className="px-6 py-4 font-medium text-white">
                        {t.playerName || t.player?.name || "Unknown"}
                      </td>
                      <td className="px-6 py-4">{t.fromClub || t.fromTeam?.name || "—"}</td>
                      <td className="px-6 py-4">{t.toClub || t.toTeam?.name || "—"}</td>
                      <td className="px-6 py-4 text-[#FF6B00] font-semibold">
                        {formatFee(t.transferFee || t.fee)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default TransferPage;
