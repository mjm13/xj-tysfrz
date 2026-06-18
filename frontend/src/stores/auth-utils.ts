export const AUTH_STORAGE_KEY = 'zbpt-auth'

export interface AuthPersistedState {
  isAuthenticated: boolean
  username: string
}

export function loadAuthFromStorage(): AuthPersistedState {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    if (!raw) {
      return { isAuthenticated: false, username: '' }
    }
    const parsed = JSON.parse(raw) as Partial<AuthPersistedState>
    return {
      isAuthenticated: Boolean(parsed.isAuthenticated),
      username: typeof parsed.username === 'string' ? parsed.username : '',
    }
  } catch {
    return { isAuthenticated: false, username: '' }
  }
}

export function saveAuthToStorage(state: AuthPersistedState): void {
  localStorage.setItem(
    AUTH_STORAGE_KEY,
    JSON.stringify({
      isAuthenticated: state.isAuthenticated,
      username: state.username,
    }),
  )
}

export function clearAuthStorage(): void {
  localStorage.removeItem(AUTH_STORAGE_KEY)
}

export function validateMockCredentials(username: string, password: string): boolean {
  return username.trim().length > 0 && password.trim().length > 0
}
