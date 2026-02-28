import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Log env at startup to verify Vite is loading variables
// eslint-disable-next-line no-console
console.info('[env] import.meta.env snapshot', {
  VITE_API_BASE: import.meta.env.VITE_API_BASE,
  VITE_API_TIMEOUT: import.meta.env.VITE_API_TIMEOUT,
  MODE: import.meta.env.MODE,
});

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
