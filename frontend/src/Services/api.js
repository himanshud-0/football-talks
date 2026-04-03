import { API_BASE } from "../config";

const requestJson = async (path) => {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    throw new Error(`Request failed with status ${res.status}`);
  }
  return res.json();
};

export const getPlayers = async (params = {}) => {
  const query = new URLSearchParams();
  if (params.search) query.set("search", params.search);
  if (params.position) query.set("position", params.position);
  if (params.teamId) query.set("teamId", params.teamId);
  if (params.league) query.set("league", params.league);
  if (params.limit) query.set("limit", params.limit);
  const qs = query.toString();
  return requestJson(`/api/players${qs ? `?${qs}` : ""}`);
};

export const getPlayer = async (id) => {
  return requestJson(`/api/players/${id}`);
};

export const getTransfers = async () => {
  return requestJson("/api/transfers?limit=50");
};

export const getTeams = async () => {
  return requestJson("/api/teams");
};

export const getCompetitions = async () => {
  return requestJson("/api/competitions");
};

export const getPosts = async () => {
  return requestJson("/api/posts");
};
