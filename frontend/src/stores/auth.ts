import { defineStore } from 'pinia'
import {
  clearAuthStorage,
  loadAuthFromStorage,
  saveAuthToStorage,
  validateMockCredentials,
} from './auth-utils'

export const useAuthStore = defineStore('auth', {
  state: () => loadAuthFromStorage(),
  actions: {
    login(username: string, password: string): boolean {
      if (!validateMockCredentials(username, password)) {
        return false
      }
      this.isAuthenticated = true
      this.username = username.trim()
      saveAuthToStorage({
        isAuthenticated: this.isAuthenticated,
        username: this.username,
      })
      return true
    },
    logout() {
      this.isAuthenticated = false
      this.username = ''
      clearAuthStorage()
    },
  },
})
