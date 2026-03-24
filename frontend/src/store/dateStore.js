import { create } from 'zustand'
import dayjs from 'dayjs'

const today = dayjs().format('YYYY-MM-DD')
const tomorrow = dayjs().add(1, 'day').format('YYYY-MM-DD')

const useDateStore = create((set) => ({
  checkIn: today,
  checkOut: tomorrow,
  setDates: (checkIn, checkOut) => set({ checkIn, checkOut }),
}))

export default useDateStore
