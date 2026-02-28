/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#FF6B00',
        'primary-hover': '#FF8533',
        dark: '#0F0F0F',
        'dark-card': '#1A1A1A',
      }
    },
  },
  plugins: [],
}
