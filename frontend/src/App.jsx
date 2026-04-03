import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import HomePage from './Pages/HomePage.jsx'
import PlayersPage from './Pages/PlayersPage.jsx'
import PlayerDetailPage from './Pages/PlayerDetailPage.jsx'
import TransferPage from './Pages/TransferPage.jsx'
import LoginPage from './Pages/LoginPage.jsx'
import SignUpPage from './Pages/SignUpPage.jsx'
import NewsPage from './Pages/NewsPage.jsx'
import LeaguesPage from './Pages/LeaguesPage.jsx'
import ClubDetailPage from './Pages/ClubDetailPage.jsx'
import StandingsPage from './Pages/StandingsPage.jsx'
import FixturesPage from './Pages/FixturesPage.jsx'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/players" element={<PlayersPage />} />
        <Route path="/players/:id" element={<PlayerDetailPage />} />
        <Route path="/news" element={<NewsPage />} />
        <Route path="/leagues" element={<LeaguesPage />} />
        <Route path="/clubs/:id" element={<ClubDetailPage />} />
        <Route path="/standings" element={<StandingsPage />} />
        <Route path="/fixtures" element={<FixturesPage />} />
        <Route path="/transfers" element={<TransferPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
