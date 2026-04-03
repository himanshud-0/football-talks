import { API_BASE } from "../config";

const requestJson = async (path) => {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    throw new Error(`Request failed with status ${res.status}`);
  }
  return res.json();
};

// Players
export const getPlayers = async (params = {}) => {
  const query = new URLSearchParams();
  if (params.search) query.set("search", params.search);
  if (params.position) query.set("position", params.position);
  if (params.teamId) query.set("teamId", params.teamId);
  if (params.competitionId) query.set("competitionId", params.competitionId);
  if (params.league) query.set("league", params.league);
  if (params.limit) query.set("limit", params.limit);
  if (params.sortBy) query.set("sortBy", params.sortBy);
  if (params.direction) query.set("direction", params.direction);
  const qs = query.toString();
  return requestJson(`/api/players${qs ? `?${qs}` : ""}`);
};

export const getPlayer = async (id) => {
  return requestJson(`/api/players/${id}`);
};

// Transfers
export const getTransfers = async (params = {}) => {
  const query = new URLSearchParams();
  if (params.limit) query.set("limit", params.limit || 50);
  if (params.season) query.set("season", params.season);
  if (params.league) query.set("league", params.league);
  const qs = query.toString();
  return requestJson(`/api/transfers${qs ? `?${qs}` : "?limit=50"}`);
};

// Teams / Clubs
export const getTeams = async (params = {}) => {
  const query = new URLSearchParams();
  if (params.search) query.set("search", params.search);
  if (params.competitionId) query.set("competitionId", params.competitionId);
  if (params.limit) query.set("limit", params.limit);
  const qs = query.toString();
  return requestJson(`/api/teams${qs ? `?${qs}` : ""}`);
};

export const getTeam = async (id) => {
  return requestJson(`/api/teams/${id}`);
};

// Competitions / Leagues
export const getCompetitions = async () => {
  return requestJson("/api/competitions");
};

// News / Posts
export const getPosts = async () => {
  return requestJson("/api/posts");
};

export const getPost = async (id) => {
  return requestJson(`/api/posts/${id}`);
};

// Auth (authenticated requests)
const authRequest = async (path, method = "GET", body = null) => {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const options = { method, headers };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE}${path}`, options);
  if (!res.ok) throw new Error(`Request failed: ${res.status}`);
  return res.json();
};

export const createPost = async (data) => authRequest("/api/posts", "POST", data);
export const deletePost = async (id) => authRequest(`/api/posts/${id}`, "DELETE");
export const createComment = async (postId, data) => authRequest(`/api/posts/${postId}/comments`, "POST", data);
export const getUserProfile = async () => authRequest("/api/users/me");
