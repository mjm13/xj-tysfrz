const PATH_MODULE_MAP: Record<string, string> = {
  '/': 'home',
  '/admin/users': 'platform-admin',
  '/admin/roles': 'platform-admin',
  '/identity/basic': 'identity-basic',
  '/identity/classification': 'identity-classification',
  '/identity/position': 'identity-position',
  '/identity/tags': 'identity-tags',
  '/identity/org': 'identity-org',
  '/identity/permission': 'identity-permission',
  '/services/query': 'services-query',
  '/services/etl': 'services-etl',
  '/services/sources': 'services-sources',
}

const PATH_PERMISSION_MAP: Record<string, string> = {
  '/admin/users': 'admin:users:read',
  '/admin/roles': 'admin:roles:read',
}

const PLATFORM_ADMIN_READ_PERMISSIONS = ['admin:users:read', 'admin:roles:read'] as const

export function moduleKeyForPath(path: string): string | null {
  if (PATH_MODULE_MAP[path]) {
    return PATH_MODULE_MAP[path]
  }
  for (const [prefix, moduleKey] of Object.entries(PATH_MODULE_MAP)) {
    if (prefix !== '/' && path.startsWith(`${prefix}/`)) {
      return moduleKey
    }
  }
  return null
}

export function permissionForPath(path: string): string | null {
  if (PATH_PERMISSION_MAP[path]) {
    return PATH_PERMISSION_MAP[path]
  }
  const moduleKey = moduleKeyForPath(path)
  if (!moduleKey) {
    return null
  }
  return permissionForModule(moduleKey)
}

export function permissionForModule(moduleKey: string): string {
  return `${moduleKey}:read`
}

export function canAccessPath(permissions: ReadonlySet<string> | string[], path: string): boolean {
  const required = permissionForPath(path)
  if (!required) {
    return true
  }
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has(required)
}

export function canAccessModule(permissions: ReadonlySet<string> | string[], moduleKey: string): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  if (moduleKey === 'platform-admin') {
    return PLATFORM_ADMIN_READ_PERMISSIONS.some((perm) => set.has(perm))
  }
  return set.has(permissionForModule(moduleKey))
}

export function canWriteAdminUsers(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:users:write')
}

export function canWriteAdminRoles(permissions: ReadonlySet<string> | string[]): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has('admin:roles:write')
}
