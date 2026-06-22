export const AUTH_STORAGE_KEY = 'tysfrz-auth'

export interface AuthPersistedState {
  accessToken: string
  username: string
}

export function loadAuthFromStorage(): AuthPersistedState | null {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY)
    if (!raw) {
      return null
    }
    const parsed = JSON.parse(raw) as Partial<AuthPersistedState>
    if (typeof parsed.accessToken !== 'string' || !parsed.accessToken) {
      return null
    }
    return {
      accessToken: parsed.accessToken,
      username: typeof parsed.username === 'string' ? parsed.username : '',
    }
  } catch {
    return null
  }
}

export function saveAuthToStorage(state: AuthPersistedState): void {
  localStorage.setItem(
    AUTH_STORAGE_KEY,
    JSON.stringify({
      accessToken: state.accessToken,
      username: state.username,
    }),
  )
}

export function clearAuthStorage(): void {
  localStorage.removeItem(AUTH_STORAGE_KEY)
}
