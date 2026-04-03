import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "https://football-talks.onrender.com";

const formatFee = (fee) => {
  if (!fee || fee === 0) return "Free";
  if (typeof fee === "string") return fee;
  if (fee >= 1_000_000) return `€${(fee / 1_000_000).toFixed(0)}M`;
  if (fee >= 1_000) return `€${(fee / 1_000).toFixed(0)}K`;
  return `€${fee}`;
};

const mapTransfer = (t) => {
  if (!t || typeof t !== "object") return null;
  return {
    id: t.id || t.transferId,
    player: t.playerName || t.player_name || t.player || "Unknown",
    from: t.fromClub || t.fromTeam?.name || t.from_team_name || t.from || "—",
    to: t.toClub || t.toTeam?.name || t.to_team_name || t.to || "—",
    fee: formatFee(t.transferFee || t.fee || t.transferFeeFormatted),
  };
};

const TransferTable = ({ showAll = false }) => {
  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${API_BASE}/api/transfers`)
      .then((res) => res.json())
      .then((data) => {
        const list = Array.isArray(data) ? data : data?.content || data?.data || [];
        const mapped = list.map(mapTransfer).filter(Boolean);
        setTransfers(mapped);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  const visible = showAll ? transfers : transfers.slice(0, 5);

  if (loading) {
    return (
      <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 pb-10">
        <h1 className="w-full mb-6 text-2xl font-bold text-[#FFFFFF]">Recent Transfers</h1>
        <div className="text-center py-10 text-[#B3B3B3]">
          <div className="inline-block w-6 h-6 border-2 border-[#FF6B00] border-t-transparent rounded-full animate-spin mb-2"></div>
          <p className="text-sm">Loading transfers...</p>
        </div>
      </section>
    );
  }

  if (transfers.length === 0) {
    return (
      <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 pb-10">
        <h1 className="w-full mb-6 text-2xl font-bold text-[#FFFFFF]">Recent Transfers</h1>
        <div className="text-center py-10 text-[#B3B3B3] bg-[#1A1A1A] rounded-2xl border border-[#0F0F0F]">
          <p className="text-2xl mb-2">📋</p>
          <p>No transfer data yet. Check back soon.</p>
        </div>
      </section>
    );
  }

  return (
    <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 pb-10">
      <h1 className="w-full mb-6 text-2xl font-bold text-[#FFFFFF]">Recent Transfers</h1>
      <div className="overflow-x-auto rounded-2xl border border-[#1A1A1A] shadow-lg bg-[#1A1A1A]">
        <table className="w-full min-w-[480px] border-collapse">
          <thead className="bg-[#0F0F0F]">
            <tr className="text-[#FF6B00] font-bold">
              <th className="px-4 py-3 text-left">Player</th>
              <th className="px-4 py-3 text-left">From Club</th>
              <th className="px-4 py-3 text-left">To Club</th>
              <th className="px-4 py-3 text-left">Transfer Fee</th>
            </tr>
          </thead>
          <tbody>
            {visible.map((t, i) => (
              <tr
                key={t.id || i}
                className="hover:bg-[#262626] border-b border-[#0F0F0F] text-[#B3B3B3] transition-colors"
              >
                <td className="px-4 py-3 text-[#FFFFFF] font-medium">{t.player}</td>
                <td className="px-4 py-3">{t.from}</td>
                <td className="px-4 py-3">{t.to}</td>
                <td className="px-4 py-3 text-[#FF6B00] font-semibold">{t.fee}</td>
              </tr>
            ))}
            {!showAll && (
              <tr className="border-b border-[#0F0F0F]">
                <td colSpan={4} className="px-4 py-3 bg-[#0F0F0F] text-center">
                  <Link
                    to="/transfers"
                    className="inline-block text-sm font-semibold text-[#FF6B00] hover:text-[#FF8533]"
                  >
                    See all transfers →
                  </Link>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default TransferTable;
