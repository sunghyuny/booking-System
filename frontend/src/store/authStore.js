import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const useAuthStore = create(
  persist(
    (set) => ({
      isLoggedIn: false,
      user: null,
      setLoggedIn: (user) => set({ isLoggedIn: true, user }),
      logout: () => set({ isLoggedIn: false, user: null }),
    }),
    { name: 'auth-store' }
  )
)

export default useAuthStore
