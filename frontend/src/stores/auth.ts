import { defineStore } from 'pinia'
import { login as loginApi, fetchMe, logoutApi, type UserProfile } from '@/api/auth'
import { setTokenGetter } from '@/api/client'
import { canAccessModule, canAccessPath } from '@/config/permissions'
import {
  clearAuthStorage,
  loadAuthFromStorage,
  saveAuthToStorage,
} from './auth-utils'

interface AuthState {
  accessToken: string
  username: string
  profile: UserProfile | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    const persisted = loadAuthFromStorage()
    return {
      accessToken: persisted?.accessToken ?? '',
      username: persisted?.username ?? '',
      profile: null,
    }
  },
  getters: {
    isAuthenticated: (state) => Boolean(state.accessToken),
    permissions: (state) => new Set(state.profile?.permissions ?? []),
  },
  actions: {
    bindTokenGetter() {
      setTokenGetter(() => this.accessToken || null)
    },
    async login(username: string, password: string): Promise<void> {
      const result = await loginApi(username, password)
      this.accessToken = result.accessToken
      this.username = result.profile.username
      this.profile = result.profile
      saveAuthToStorage({ accessToken: this.accessToken, username: this.username })
      this.bindTokenGetter()
    },
    async restoreSession(): Promise<boolean> {
      if (!this.accessToken) {
        return false
      }
      this.bindTokenGetter()
      try {
        this.profile = await fetchMe()
        this.username = this.profile.username
        return true
      } catch {
        this.clearSession()
        return false
      }
    },
    async logout(): Promise<void> {
      this.bindTokenGetter()
      try {
        if (this.accessToken) {
          await logoutApi()
        }
      } catch {
        // ignore logout API errors
      } finally {
        this.clearSession()
      }
    },
    clearSession() {
      this.accessToken = ''
      this.username = ''
      this.profile = null
      clearAuthStorage()
      this.bindTokenGetter()
    },
    canAccessModule(moduleKey: string): boolean {
      if (!this.profile) {
        return false
      }
      return canAccessModule(this.profile.permissions, moduleKey)
    },
    canAccessPath(path: string): boolean {
      if (!this.profile) {
        return false
      }
      return canAccessPath(this.profile.permissions, path)
    },
  },
})
