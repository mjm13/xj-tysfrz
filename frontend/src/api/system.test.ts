import { describe, expect, it } from 'vitest'
import { isVersionCompatible } from './system'

describe('isVersionCompatible', () => {
  it('returns true when versions match', () => {
    expect(isVersionCompatible('0.2.0', '0.2.0')).toBe(true)
  })

  it('returns false when versions differ', () => {
    expect(isVersionCompatible('0.2.0', '0.1.0')).toBe(false)
  })
})
