import { request } from './client'

export interface UserProfile {
  platformUserId: string
  username: string
  roles: string[]
  permissions: string[]
  dataScope: string
  departmentCode: string
  globalScope: boolean
  scopedDeptCodes: string[]
}

export interface LoginResult {
  accessToken: string
  profile: UserProfile
}

export function login(username: string, password: string): Promise<LoginResult> {
  return request<LoginResult>('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
}

export function fetchMe(): Promise<UserProfile> {
  return request<UserProfile>('/api/auth/me')
}

export function logoutApi(): Promise<void> {
  return request<void>('/api/auth/logout', { method: 'POST' })
}
