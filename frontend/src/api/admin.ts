import { request } from './client'

export interface UserSummary {
  platformUserId: string
  username: string
  status: string
  departmentCode: string
  dataScope: string
}

export interface OrgNodeSummary {
  code: string
  name: string
  parentCode: string | null
}

export interface CreateUserPayload {
  username: string
  password: string
  departmentCode: string
  dataScope: string
}

export function listUsers(): Promise<UserSummary[]> {
  return request<UserSummary[]>('/api/admin/users')
}

export function createUser(payload: CreateUserPayload): Promise<UserSummary> {
  return request<UserSummary>('/api/admin/users', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function listOrgNodes(): Promise<OrgNodeSummary[]> {
  return request<OrgNodeSummary[]>('/api/admin/org-nodes')
}

export function canWriteUsers(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:users:write')
}
