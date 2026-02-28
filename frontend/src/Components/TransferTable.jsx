import React from "react";
import { Link } from "react-router-dom";

const transfers = [
  {
    player: "Cristiano Ronaldo",
    from: "Juventus",
    to: "Manchester United",
    fee: "EUR 35 million",
  },
  {
    player: "Lionel Messi",
    from: "FC Barcelona",
    to: "Paris Saint-Germain",
    fee: "EUR 45 million",
  },
  {
    player: "Neymar Jr.",
    from: "FC Barcelona",
    to: "Paris Saint-Germain",
    fee: "EUR 222 million",
  },
  {
    player: "Kylian Mbappe",
    from: "Paris Saint-Germain",
    to: "Real Madrid",
    fee: "EUR 100 million",
  },
  {
    player: "Jude Bellingham",
    from: "Borussia Dortmund",
    to: "Real Madrid",
    fee: "EUR 103 million",
  },
  {
    player: "Declan Rice",
    from: "West Ham United",
    to: "Arsenal",
    fee: "EUR 116 million",
  },
  {
    player: "Harry Kane",
    from: "Tottenham Hotspur",
    to: "Bayern Munich",
    fee: "EUR 100 million",
  },
  {
    player: "Moises Caicedo",
    from: "Brighton",
    to: "Chelsea",
    fee: "EUR 116 million",
  },
];

const TransferTable = ({ showAll = false }) => {
  const previewCount = 5;
  const visibleTransfers = showAll ? transfers : transfers.slice(0, previewCount);

  return (
    <section className="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 pb-10">
      <h1 className="w-full mb-6 text-2xl font-bold text-[#FFFFFF]">Recent Transfers</h1>
      <div className="overflow-x-auto rounded-2xl border border-[#1A1A1A] shadow-lg bg-[#1A1A1A]">
        <table className="w-full min-w-160 border-collapse">
          <thead className="bg-[#0F0F0F]">
            <tr className="text-[#FF6B00] font-bold">
              <th className="px-4 py-3 text-left">Player</th>
              <th className="px-4 py-3 text-left">From Club</th>
              <th className="px-4 py-3 text-left">To Club</th>
              <th className="px-4 py-3 text-left">Transfer Fee</th>
            </tr>
          </thead>
          <tbody>
            {visibleTransfers.map((transfer) => (
              <tr key={transfer.player} className="hover:bg-[#262626] border-b border-[#0F0F0F] text-[#B3B3B3]">
                <td className="px-4 py-3 text-[#FFFFFF]">{transfer.player}</td>
                <td className="px-4 py-3">{transfer.from}</td>
                <td className="px-4 py-3">{transfer.to}</td>
                <td className="px-4 py-3">{transfer.fee}</td>
              </tr>
            ))}
            {!showAll ? (
              <tr className="border-b border-[#0F0F0F]">
                <td colSpan={4} className="px-4 py-3 bg-[#0F0F0F] text-center">
                  <Link
                    to="/transfers"
                    className="inline-block text-sm font-semibold text-[#FF6B00] hover:text-[#FF8533]"
                  >
                    See more
                  </Link>
                </td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>
    </section>
  );
};

export default TransferTable;
