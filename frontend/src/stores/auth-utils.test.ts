import { describe, expect, it, beforeEach, vi } from 'vitest'
import {
  AUTH_STORAGE_KEY,
  clearAuthStorage,
  loadAuthFromStorage,
  saveAuthToStorage,
} from './auth-utils'

function createStorage(): Storage {
  const store = new Map<string, string>()
  return {
    get length() {
      return store.size
    },
    clear() {
      store.clear()
    },
    getItem(key: string) {
      return store.get(key) ?? null
    },
    key(index: number) {
      return [...store.keys()][index] ?? null
    },
    removeItem(key: string) {
      store.delete(key)
    },
    setItem(key: string, value: string) {
      store.set(key, value)
    },
  }
}

describe('auth-utils', () => {
  beforeEach(() => {
    vi.stubGlobal('localStorage', createStorage())
  })

  it('persists token without password', () => {
    saveAuthToStorage({ accessToken: 'jwt-token', username: 'admin' })
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    expect(raw).toBeTruthy()
    expect(raw).not.toContain('password')
    expect(loadAuthFromStorage()).toEqual({ accessToken: 'jwt-token', username: 'admin' })
  })

  it('returns null when token missing', () => {
    expect(loadAuthFromStorage()).toBeNull()
  })

  it('clears auth storage on logout', () => {
    saveAuthToStorage({ accessToken: 'jwt-token', username: 'admin' })
    clearAuthStorage()
    expect(loadAuthFromStorage()).toBeNull()
  })
})
