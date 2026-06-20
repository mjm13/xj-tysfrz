import { describe, expect, it } from 'vitest'
import { SYSU_CLASSIFICATION } from './sysu-cls'
import { ORG_BY_CODE, ORG_UNITS, SYSU_ORG, orgChildren, orgName } from './sysu-org'

describe('demo mock data', () => {
  it('exports non-empty classification tree', () => {
    expect(SYSU_CLASSIFICATION.length).toBeGreaterThan(0)
    expect(SYSU_CLASSIFICATION[0]?.code).toBeTruthy()
  })

  it('exports non-empty org tree with helpers', () => {
    expect(SYSU_ORG.length).toBeGreaterThan(0)
    expect(ORG_UNITS.length).toBeGreaterThan(0)
    expect(ORG_BY_CODE.SYSU?.name).toBe('中山大学')
    expect(orgChildren('SYSU').length).toBeGreaterThan(0)
    expect(orgName('SYSU')).toBe('中山大学')
    expect(orgName('missing')).toBe('missing')
  })
})
