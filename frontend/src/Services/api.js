import { API_BASE } from "../config";

const requestJson = async (path) => {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    throw new Error(`Request failed with status ${res.status}`);
  }
  return res.json();
};

export const getPlayers = async () => {
  return requestJson("/players");
};

export const getTransfers = async () => {
  return requestJson("/transfers?limit=50");
};
