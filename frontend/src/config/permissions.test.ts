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
})
