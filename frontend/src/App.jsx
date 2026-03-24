import { BrowserRouter, Routes, Route, Link, useNavigate } from 'react-router-dom'
import HotelList from './pages/HotelList'
import HotelDetail from './pages/HotelDetail'
import Reserve from './pages/Reserve'
import Login from './pages/Login'
import Signup from './pages/Signup'
import MyReservations from './pages/MyReservations'
import useAuthStore from './store/authStore'
import { authApi } from './api'

function Navbar() {
  const { isLoggedIn, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = async () => {
    try {
      await authApi.logout()
    } finally {
      logout()
      navigate('/login')
    }
  }

  return (
    <nav className="navbar">
      <Link to="/" className="nav-logo">🏨 StayBook</Link>
      <div className="nav-links">
        <Link to="/">호텔</Link>
        {isLoggedIn ? (
          <>
            <Link to="/my-reservations">내 예약</Link>
            <button className="btn-logout" onClick={handleLogout}>로그아웃</button>
          </>
        ) : (
          <>
            <Link to="/login">로그인</Link>
            <Link to="/signup" className="btn-signup">회원가입</Link>
          </>
        )}
      </div>
    </nav>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HotelList />} />
          <Route path="/hotels/:id" element={<HotelDetail />} />
          <Route path="/reserve/:roomId" element={<Reserve />} />
          <Route path="/my-reservations" element={<MyReservations />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
