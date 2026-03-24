import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { hotelApi } from '../api'
import useDateStore from '../store/dateStore'

export default function HotelList() {
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const { checkIn, checkOut, setDates } = useDateStore()

  useEffect(() => {
    setLoading(true)
    const params = new URLSearchParams()
    if (checkIn) params.append('checkInDate', checkIn)
    if (checkOut) params.append('checkOutDate', checkOut)

    hotelApi.getAll(params.toString())
      .then(res => setHotels(res.data))
      .catch(() => setError('호텔 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [checkIn, checkOut])

  if (loading) return <div className="loading">🔄 호텔 목록 불러오는 중...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="page">
      <h1 className="page-title">🏨 호텔 예약</h1>
      <div className="search-bar" style={{ display: 'flex', gap: '15px', marginBottom: '25px', backgroundColor: 'var(--surface)', padding: '15px', borderRadius: '12px', border: '1px solid var(--border)' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>체크인</label>
          <input type="date" value={checkIn} onChange={e => setDates(e.target.value, checkOut)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid var(--border)', background: 'var(--surface2)', color: 'var(--text)' }} />
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>체크아웃</label>
          <input type="date" value={checkOut} onChange={e => setDates(checkIn, e.target.value)} style={{ padding: '8px', borderRadius: '6px', border: '1px solid var(--border)', background: 'var(--surface2)', color: 'var(--text)' }} />
        </div>
      </div>
      <div className="hotel-grid">
        {hotels.length === 0 ? (
          <p className="empty">등록된 호텔이 없습니다.</p>
        ) : (
          hotels.map(hotel => (
            <div
              key={hotel.id}
              className={`hotel-card ${hotel.roomCount === -1 ? 'sold-out' : ''}`}
              style={{ opacity: hotel.roomCount === -1 ? 0.5 : 1, pointerEvents: hotel.roomCount === -1 ? 'none' : 'auto' }}
              onClick={() => navigate(`/hotels/${hotel.id}`)}
            >
              <h2>{hotel.name}</h2>
              <p className="hotel-location">📍 {hotel.address}</p>
              <p className="hotel-desc">{hotel.description}</p>
              {hotel.roomCount === -1 ? (
                <p className="hotel-rooms" style={{ color: 'var(--error)', fontWeight: 'bold' }}>🚫 지정된 일자에 예약 가능한 객실이 없습니다</p>
              ) : (
                <p className="hotel-rooms">🛏️ 예약 가능 객실: {hotel.roomCount}개</p>
              )}
              <button className="btn-primary" disabled={hotel.roomCount === -1}>
                {hotel.roomCount === -1 ? '예약 마감' : '객실 보기'}
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
