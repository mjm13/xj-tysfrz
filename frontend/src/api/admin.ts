import { request } from './client'

export interface MenuNode {
  menuCode: string
  label: string
  path: string | null
  parentCode: string | null
  sortOrder: number
  menuType: 'LINK' | 'GROUP' | string
  moduleKey: string | null
  visible: boolean
  description: string | null
  permissionCodes: string[]
  children: MenuNode[]
}

export interface CreateMenuPayload {
  menuCode: string
  label: string
  path?: string | null
  parentCode?: string | null
  sortOrder: number
  menuType: 'LINK' | 'GROUP'
  moduleKey?: string | null
  description?: string | null
  permissionCodes: string[]
}

export interface UpdateMenuPayload {
  label: string
  path?: string | null
  parentCode?: string | null
  sortOrder: number
  menuType: 'LINK' | 'GROUP'
  moduleKey?: string | null
  description?: string | null
  permissionCodes?: string[]
}

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
  hasChildren?: boolean
}

export interface RoleSummary {
  roleCode: string
  name: string
  description: string | null
  permissionCodes: string[]
  systemRole: boolean
  permissionsEditable: boolean
}

export interface PermissionSummary {
  permissionCode: string
  moduleName: string
  actionName: string
}

export interface CreateUserPayload {
  username: string
  password: string
  departmentCode: string
  dataScope: string
}

export interface CreateRolePayload {
  roleCode: string
  name: string
  description?: string
}

export interface CreateOrgNodePayload {
  code: string
  name: string
  parentCode: string
}

export interface UpdateOrgNodePayload {
  name: string
  parentCode?: string
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

export function listOrgNodeRoots(): Promise<OrgNodeSummary[]> {
  return request<OrgNodeSummary[]>('/api/admin/org-nodes/roots')
}

export function listOrgNodeChildren(parentCode: string): Promise<OrgNodeSummary[]> {
  return request<OrgNodeSummary[]>(`/api/admin/org-nodes/children?parentCode=${encodeURIComponent(parentCode)}`)
}

export function createOrgNode(payload: CreateOrgNodePayload): Promise<OrgNodeSummary> {
  return request<OrgNodeSummary>('/api/admin/org-nodes', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateOrgNode(code: string, payload: UpdateOrgNodePayload): Promise<OrgNodeSummary> {
  return request<OrgNodeSummary>(`/api/admin/org-nodes/${encodeURIComponent(code)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function listRoles(): Promise<RoleSummary[]> {
  return request<RoleSummary[]>('/api/admin/roles')
}

export function createRole(payload: CreateRolePayload): Promise<RoleSummary> {
  return request<RoleSummary>('/api/admin/roles', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function replaceRolePermissions(roleCode: string, permissionCodes: string[]): Promise<RoleSummary> {
  return request<RoleSummary>(`/api/admin/roles/${encodeURIComponent(roleCode)}/permissions`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ permissionCodes }),
  })
}

export function listPermissions(): Promise<PermissionSummary[]> {
  return request<PermissionSummary[]>('/api/admin/permissions')
}

export function listMenus(): Promise<MenuNode[]> {
  return request<MenuNode[]>('/api/admin/menus')
}

export function listMenuPermissionTree(): Promise<MenuNode[]> {
  return request<MenuNode[]>('/api/admin/menus/permission-tree')
}

export function createMenu(payload: CreateMenuPayload): Promise<MenuNode> {
  return request<MenuNode>('/api/admin/menus', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateMenu(menuCode: string, payload: UpdateMenuPayload): Promise<MenuNode> {
  return request<MenuNode>(`/api/admin/menus/${encodeURIComponent(menuCode)}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export function updateMenuVisible(menuCode: string, visible: boolean): Promise<MenuNode> {
  return request<MenuNode>(`/api/admin/menus/${encodeURIComponent(menuCode)}/visible`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ visible }),
  })
}

export function canWriteMenus(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:menus:write')
}

export function canWriteUsers(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:users:write')
}

export function canWriteRoles(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:roles:write')
}

export function canWriteDepartments(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:departments:write')
}
