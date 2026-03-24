import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true, // HttpOnly 쿠키 자동 전송
})

export const authApi = {
  signup: (data) => api.post('/auth/signup', data),
  login: (data) => api.post('/auth/login', data),
  logout: () => api.post('/auth/logout'),
}

export const hotelApi = {
  getAll: () => api.get('/hotels'),
  getRooms: (hotelId) => api.get(`/hotels/${hotelId}/rooms`),
}

export const reservationApi = {
  create: (data) => api.post('/reservations', data),
  getMyReservations: () => api.get('/reservations/me'),
  getBookedDates: (roomId) => api.get(`/reservations/rooms/${roomId}/booked-dates`),
}

export default api
