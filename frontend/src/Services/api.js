import { API_BASE } from "../config";

export const getPlayers = async () => {
  const res = await fetch(`${API_BASE}/players`);
  if (!res.ok) {
    throw new Error(`Players request failed with status ${res.status}`);
  }
  return res.json();
};
