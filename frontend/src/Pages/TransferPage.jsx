import React from "react";
import Header from "../Components/Header";
import Footer from "../Components/Footer";
import TransferTable from "../Components/TransferTable";

const TransferPage = () => {
  return (
    <div className="transfer-page-bg min-h-screen flex flex-col">
      <Header />
      <main className="flex-1">
        <div className="px-4 sm:px-6 pt-10 sm:pt-12 pb-8">
          <h1 className="transfer-title text-3xl sm:text-4xl font-bold text-center text-[#FF6B00]">
            Transfer News
          </h1>
          <p
            className="transfer-subtitle text-center text-[#B3B3B3] mt-3"
            style={{ animationDelay: "120ms" }}
          >
            Latest completed and trending transfer moves.
          </p>
        </div>
        <div className="transfer-table-wrap">
          <TransferTable showAll />
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default TransferPage;
