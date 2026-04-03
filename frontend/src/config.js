const apiBase = import.meta.env.VITE_API_BASE;
const apiTimeoutRaw = import.meta.env.VITE_API_TIMEOUT;

export const API_BASE =
  typeof apiBase === "string" && apiBase.trim()
    ? apiBase
    : "https://football-talks.onrender.com";

export const API_TIMEOUT = Number(apiTimeoutRaw ?? 0) || 5000;

// eslint-disable-next-line no-console
console.info("[env] Using API base:", API_BASE);
