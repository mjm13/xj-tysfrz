import { describe, expect, it, beforeEach, vi } from 'vitest'
import {
  AUTH_STORAGE_KEY,
  clearAuthStorage,
  loadAuthFromStorage,
  saveAuthToStorage,
  validateMockCredentials,
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

  it('rejects empty credentials', () => {
    expect(validateMockCredentials('', 'password')).toBe(false)
    expect(validateMockCredentials('user', '')).toBe(false)
    expect(validateMockCredentials('  ', 'password')).toBe(false)
  })

  it('accepts non-empty credentials', () => {
    expect(validateMockCredentials('admin', 'admin')).toBe(true)
  })

  it('persists auth state without password', () => {
    saveAuthToStorage({ isAuthenticated: true, username: 'admin' })
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    expect(raw).toBeTruthy()
    expect(raw).not.toContain('password')
    expect(loadAuthFromStorage()).toEqual({ isAuthenticated: true, username: 'admin' })
  })

  it('clears auth storage on logout', () => {
    saveAuthToStorage({ isAuthenticated: true, username: 'admin' })
    clearAuthStorage()
    expect(loadAuthFromStorage()).toEqual({ isAuthenticated: false, username: '' })
  })
})
