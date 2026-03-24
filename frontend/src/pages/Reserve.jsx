import { useState, useEffect } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { reservationApi } from '../api'
import dayjs from 'dayjs'
import useDateStore from '../store/dateStore'

export default function Reserve() {
  const { roomId } = useParams()
  const { state } = useLocation()
  const navigate = useNavigate()
  const room = state?.room

  const today = dayjs().format('YYYY-MM-DD')
  const { checkIn, checkOut, setDates } = useDateStore()
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState(null)
  
  const [guestName, setGuestName] = useState('')
  const [guestPhone, setGuestPhone] = useState('')
  const [guestCount, setGuestCount] = useState(room ? room.capacity : 2)
  const [specialRequests, setSpecialRequests] = useState('')

  useEffect(() => {
    if (room && !guestCount) {
      setGuestCount(room.capacity)
    }
  }, [room])

  const nights = dayjs(checkOut).diff(dayjs(checkIn), 'day')
  const basePrice = (room?.price || 0) * Math.max(nights, 0)
  const extraGuests = Math.max(0, guestCount - (room?.capacity || 2))
  const extraFee = extraGuests * 20000 * Math.max(nights, 0)
  const totalPrice = basePrice + extraFee

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (nights <= 0) {
      setMessage({ type: 'error', text: '체크아웃은 체크인 이후 날짜여야 합니다.' })
      return
    }
    setLoading(true)
    try {
      await reservationApi.create({ 
        roomId: Number(roomId), 
        checkInDate: checkIn, 
        checkOutDate: checkOut,
        guestName,
        guestPhone,
        guestCount: Number(guestCount),
        specialRequests
      })
      setMessage({ type: 'success', text: '✅ 예약이 완료됐습니다!' })
      setTimeout(() => navigate('/'), 2000)
    } catch (err) {
      const errorText = err.response?.data?.message || (typeof err.response?.data === 'string' ? err.response?.data : '예약에 실패했습니다.')
      setMessage({ type: 'error', text: errorText })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page form-page">
      <button className="btn-back" onClick={() => navigate(-1)}>← 뒤로</button>
      <h1 className="page-title">📅 예약하기</h1>

      <div style={{ display: 'flex', gap: '40px', alignItems: 'flex-start', flexWrap: 'wrap' }}>
        {room && (
          <div className="reserve-info" style={{ flex: '1 1 400px', backgroundColor: 'var(--surface2)', padding: '2rem', borderRadius: '12px', border: '1px solid var(--border)' }}>
            <h2 style={{ marginTop: 0, color: 'var(--text)' }}>🏨 {room.hotelName}</h2>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginTop: '20px' }}>
               <p style={{ margin: 0 }}><strong>🛏 객실 유형:</strong> {room.roomType} <br/>({room.roomNumber}호)</p>
               <p style={{ margin: 0 }}><strong>👨‍👩‍👧‍👦 최대 인원:</strong> {room.capacity}명</p>
               <p style={{ margin: 0, gridColumn: '1 / -1', fontSize: '1.2rem', color: 'var(--accent)', fontWeight: 'bold', marginTop: '10px', paddingTop: '15px', borderTop: '1px solid var(--border)' }}>
                💰 1박 요금: {room.price?.toLocaleString()}원
               </p>
            </div>
          </div>
        )}

        <form className="form-box" onSubmit={handleSubmit} style={{ flex: '1 1 400px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          
          <h3 style={{ margin: 0, paddingBottom: '10px', borderBottom: '1px solid var(--border)', fontSize: '1.2rem' }}>📝 예약자 정보 입력</h3>
          <div style={{ display: 'flex', gap: '15px' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>투숙객 성명</label>
              <input type="text" value={guestName} onChange={e => setGuestName(e.target.value)} required placeholder="예: 홍길동" />
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>연락처</label>
              <input type="tel" value={guestPhone} onChange={e => setGuestPhone(e.target.value)} required placeholder="010-0000-0000" />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '15px' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>투숙 인원</label>
              <input type="number" min="1" max="10" value={guestCount} onChange={e => setGuestCount(Number(e.target.value))} required />
              {guestCount > (room?.capacity || 2) && (
                <span style={{ fontSize: '0.8rem', color: 'var(--accent)', marginTop: '4px', fontWeight: 'bold' }}>
                  기준 인원 초과 (+{(extraGuests * 20000).toLocaleString()}원/1박)
                </span>
              )}
            </div>
            <div className="form-group" style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <label>체크인 안내</label>
              <span style={{ fontSize: '0.95rem', color: 'var(--text-muted)', paddingTop: '10px' }}>체크인은 호텔 규정에 따라 당일 15:00 부터 가능합니다.</span>
            </div>
          </div>

          <div className="form-group">
            <label>별도 요청 사항</label>
            <textarea value={specialRequests} onChange={e => setSpecialRequests(e.target.value)} rows="2" placeholder="호텔에 전달할 요청사항을 적어주세요." style={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: '8px', padding: '10px 14px', color: 'var(--text)', fontSize: '0.95rem', resize: 'vertical' }}></textarea>
          </div>

          <h3 style={{ margin: 0, paddingBottom: '10px', borderBottom: '1px solid var(--border)', fontSize: '1.2rem', marginTop: '10px' }}>🗓️ 일정금액 및 결제</h3>

          <div style={{ display: 'flex', gap: '15px' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>체크인</label>
              <input type="date" value={checkIn} min={today}
                onChange={e => setDates(e.target.value, checkOut)} required />
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>체크아웃</label>
              <input type="date" value={checkOut} min={checkIn}
                onChange={e => setDates(checkIn, e.target.value)} required />
            </div>
          </div>
          
          <div style={{ display: 'flex', justifyContent: 'space-between', backgroundColor: 'var(--surface2)', padding: '20px', borderRadius: '12px', border: '1px solid var(--border)' }}>
            <div>
              <p style={{ margin: 0, color: 'var(--text-muted)' }}>체크인: <strong style={{color: 'var(--text)'}}>{checkIn}</strong></p>
              <p style={{ margin: '8px 0 0 0', color: 'var(--text-muted)' }}>체크아웃: <strong style={{color: 'var(--text)'}}>{checkOut}</strong></p>
            </div>
            <div style={{ textAlign: 'right' }}>
              <span style={{ display: 'block', marginBottom: '8px', fontSize: '0.9rem' }}>
                총 {nights}박 (인원추가 {extraFee.toLocaleString()}원)
              </span>
              <strong style={{ fontSize: '1.5rem', color: 'var(--text)' }}>{totalPrice.toLocaleString()}원</strong>
            </div>
          </div>



          {message && <div className={`msg ${message.type}`}>{message.text}</div>}

          <button type="submit" className="btn-primary full" disabled={loading} style={{ padding: '16px', fontSize: '1.1rem' }}>
            {loading ? '예약 진행 중...' : '예약 확정하기'}
          </button>
        </form>
      </div>
    </div>
  )
}
