const PATH_MODULE_MAP: Record<string, string> = {
  '/': 'home',
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

export function permissionForModule(moduleKey: string): string {
  return `${moduleKey}:read`
}

export function canAccessPath(permissions: ReadonlySet<string> | string[], path: string): boolean {
  const moduleKey = moduleKeyForPath(path)
  if (!moduleKey) {
    return true
  }
  const required = permissionForModule(moduleKey)
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has(required)
}

export function canAccessModule(permissions: ReadonlySet<string> | string[], moduleKey: string): boolean {
  const set = permissions instanceof Set ? permissions : new Set(permissions)
  return set.has(permissionForModule(moduleKey))
}
