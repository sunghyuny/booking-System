import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { hotelApi } from '../api'
import useAuthStore from '../store/authStore'

export default function HotelDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { isLoggedIn } = useAuthStore()
  const [rooms, setRooms] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    hotelApi.getRooms(id)
      .then(res => setRooms(res.data))
      .catch(() => setError('객실 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [id])

  const handleReserve = (room) => {
    if (!isLoggedIn) {
      alert('로그인이 필요합니다.')
      navigate('/login')
      return
    }
    navigate(`/reserve/${room.id}`, { state: { room } })
  }

  if (loading) return <div className="loading">🔄 객실 정보 불러오는 중...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="page">
      <button className="btn-back" onClick={() => navigate(-1)}>← 목록으로</button>
      <h1 className="page-title">🛏️ 객실 목록</h1>
      <div className="room-grid">
        {rooms.length === 0 ? (
          <p className="empty">이용 가능한 객실이 없습니다.</p>
        ) : (
          rooms.map(room => (
            <div key={room.id} className="room-card">
              <h2>{room.roomType} (호수: {room.roomNumber})</h2>
              <p className="room-price">💰 {room.price?.toLocaleString()}원 / 박</p>
              <p className="room-capacity">👨‍👩‍👧‍👦 기준 인원: {room.capacity}명</p>
              <button
                className="btn-primary"
                onClick={() => handleReserve(room)}
              >
                예약하기
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
