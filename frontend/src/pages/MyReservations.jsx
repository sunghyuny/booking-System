import { useState, useEffect } from 'react'
import { reservationApi } from '../api'

export default function MyReservations() {
  const [reservations, setReservations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    reservationApi.getMyReservations()
      .then(res => setReservations(res.data))
      .catch(() => setError('예약 내역을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="loading">🔄 예약 내역 불러오는 중...</div>
  if (error) return <div className="error">{error}</div>

  return (
    <div className="page">
      <h1 className="page-title">📝 내 예약 내역</h1>
      <div className="reservation-list" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {reservations.length === 0 ? (
          <p className="empty">예약 내역이 없습니다.</p>
        ) : (
          reservations.map(res => (
            <div key={res.reservationId} className="room-card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h3 style={{ margin: 0, paddingBottom: '5px' }}>🏨 {res.hotelName} - {res.roomNumber}호</h3>
                <p style={{ margin: '8px 0', color: '#a0a0a0', fontWeight: '500' }}>📅 {res.checkInDate} ~ {res.checkOutDate}</p>
                <p style={{ margin: 0, fontWeight: 'bold', color: '#8b5cf6', fontSize: '1.1rem' }}>💰 {res.totalPrice?.toLocaleString()}원</p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span style={{ 
                  padding: '6px 12px', borderRadius: '4px', fontSize: '0.9rem', fontWeight: 'bold',
                  backgroundColor: res.status === 'CONFIRMED' ? 'rgba(30, 215, 96, 0.2)' : 'rgba(235, 87, 87, 0.2)',
                  color: res.status === 'CONFIRMED' ? '#1ed760' : '#eb5757'
                 }}>
                  {res.status === 'CONFIRMED' ? '예약 확정' : res.status}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
