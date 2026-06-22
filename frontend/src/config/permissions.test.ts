import { describe, expect, it } from 'vitest'
import { canAccessModule, canAccessPath, moduleKeyForPath } from './permissions'

describe('permissions', () => {
  const adminPerms = new Set([
    'home:read',
    'identity-basic:read',
    'services-query:read',
  ])

  it('maps identity paths to module keys', () => {
    expect(moduleKeyForPath('/identity/basic')).toBe('identity-basic')
    expect(moduleKeyForPath('/identity/basic/changes')).toBe('identity-basic')
    expect(moduleKeyForPath('/services/query/sql')).toBe('services-query')
  })

  it('allows access when permission granted', () => {
    expect(canAccessPath(adminPerms, '/identity/basic')).toBe(true)
    expect(canAccessModule(adminPerms, 'identity-basic')).toBe(true)
  })

  it('denies access when permission missing', () => {
    expect(canAccessPath(adminPerms, '/identity/org')).toBe(false)
    expect(canAccessModule(adminPerms, 'identity-org')).toBe(false)
  })

  it('maps admin paths to platform-admin module', () => {
    expect(moduleKeyForPath('/admin/users')).toBe('platform-admin')
    expect(moduleKeyForPath('/admin/roles')).toBe('platform-admin')
    expect(moduleKeyForPath('/admin/departments')).toBe('platform-admin')
  })

  it('allows platform users when admin:users:read granted', () => {
    const perms = new Set(['admin:users:read'])
    expect(canAccessPath(perms, '/admin/users')).toBe(true)
    expect(canAccessPath(perms, '/admin/roles')).toBe(false)
    expect(canAccessModule(perms, 'platform-admin')).toBe(true)
  })

  it('allows platform roles when admin:roles:read granted', () => {
    const perms = new Set(['admin:roles:read'])
    expect(canAccessPath(perms, '/admin/roles')).toBe(true)
    expect(canAccessPath(perms, '/admin/users')).toBe(false)
    expect(canAccessPath(perms, '/admin/departments')).toBe(false)
    expect(canAccessModule(perms, 'platform-admin')).toBe(true)
  })

  it('allows platform departments when admin:departments:read granted', () => {
    const perms = new Set(['admin:departments:read'])
    expect(canAccessPath(perms, '/admin/departments')).toBe(true)
    expect(canAccessPath(perms, '/admin/users')).toBe(false)
    expect(canAccessModule(perms, 'platform-admin')).toBe(true)
  })

  it('denies platform admin without any admin read permission', () => {
    expect(canAccessPath(adminPerms, '/admin/users')).toBe(false)
    expect(canAccessPath(adminPerms, '/admin/roles')).toBe(false)
    expect(canAccessPath(adminPerms, '/admin/departments')).toBe(false)
    expect(canAccessModule(adminPerms, 'platform-admin')).toBe(false)
  })
})
