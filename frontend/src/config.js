const apiBase = import.meta.env.VITE_API_BASE;
const apiTimeoutRaw = import.meta.env.VITE_API_TIMEOUT;

export const API_BASE = typeof apiBase === 'string' ? apiBase : '';
export const API_TIMEOUT = Number(apiTimeoutRaw ?? 0) || 5000;

if (!API_BASE) {
  // eslint-disable-next-line no-console
  console.warn('[env] VITE_API_BASE is not defined. Check your .env at project root and restart dev server.');
} else {
  // eslint-disable-next-line no-console
  console.info('[env] Loaded VITE_API_BASE:', API_BASE);
}
