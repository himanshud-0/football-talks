import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import HomePage from './Pages/HomePage.jsx'
import PlayersPage from './Pages/PlayersPage.jsx'
import TransferPage from './Pages/TransferPage.jsx'
import LoginPage from './Pages/LoginPage.jsx'
import SignUpPage from './Pages/SignUpPage.jsx'
import NewsPage from './Pages/NewsPage.jsx'
import LeaguesPage from './Pages/LeaguesPage.jsx'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/players" element={<PlayersPage />} />
        <Route path="/news" element={<NewsPage />} />
        <Route path="/leagues" element={<LeaguesPage />} />
        <Route path="/transfers" element={<TransferPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
