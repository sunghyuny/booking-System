import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi } from '../api'

export default function Signup() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', name: '', role: 'USER' }) // role은 USER 고정
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleChange = e => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      await authApi.signup(form)
      alert('회원가입이 완료됐습니다! 로그인해 주세요.')
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page form-page center">
      <div className="form-box">
        <h1 className="form-title">✍️ 회원가입</h1>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>이름</label>
            <input type="text" name="name" placeholder="이름/성함"
              value={form.name} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>이메일</label>
            <input type="email" name="email" placeholder="email@example.com"
              value={form.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>비밀번호 (8자 이상)</label>
            <input type="password" name="password" placeholder="비밀번호"
              value={form.password} onChange={handleChange} minLength={8} required />
          </div>

          {error && <div className="msg error">{error}</div>}
          <button type="submit" className="btn-primary full" disabled={loading}>
            {loading ? '처리 중...' : '회원가입'}
          </button>
        </form>
        <p className="form-footer">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  )
}
