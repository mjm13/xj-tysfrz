import { afterEach, describe, expect, it, vi } from 'vitest'
import { canWriteUsers, createUser, listOrgNodes, listUsers } from './admin'

describe('admin api', () => {
  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('listUsers calls GET /api/admin/users', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        code: 0,
        data: [{ platformUserId: 'PU1', username: 'admin', status: 'ACTIVE', departmentCode: 'SYSU', dataScope: 'GLOBAL' }],
      }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const users = await listUsers()

    expect(fetchMock).toHaveBeenCalledOnce()
    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/users')
    expect(users[0].username).toBe('admin')
  })

  it('createUser posts payload to /api/admin/users', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        code: 0,
        data: { platformUserId: 'PU2', username: 'new_user', status: 'ACTIVE', departmentCode: 'CAT-party', dataScope: 'OWN_DEPT' },
      }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const created = await createUser({
      username: 'new_user',
      password: 'Passw0rd!',
      departmentCode: 'CAT-party',
      dataScope: 'OWN_DEPT',
    })

    expect(fetchMock).toHaveBeenCalledOnce()
    const init = fetchMock.mock.calls[0][1] as RequestInit
    expect(init.method).toBe('POST')
    expect(JSON.parse(init.body as string)).toMatchObject({ username: 'new_user', departmentCode: 'CAT-party' })
    expect(created.username).toBe('new_user')
  })

  it('listOrgNodes calls GET /api/admin/org-nodes', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ code: 0, data: [{ code: 'SYSU', name: '中山大学', parentCode: null }] }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const nodes = await listOrgNodes()

    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/org-nodes')
    expect(nodes[0].code).toBe('SYSU')
  })

  it('canWriteUsers checks admin:users:write permission', () => {
    expect(canWriteUsers(['admin:users:read'])).toBe(false)
    expect(canWriteUsers(['admin:users:write'])).toBe(true)
  })
})
