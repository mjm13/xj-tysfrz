import { afterEach, describe, expect, it, vi } from 'vitest'
import {
  canWriteDepartments,
  canWriteUsers,
  createOrgNode,
  createUser,
  listOrgNodeChildren,
  listOrgNodeRoots,
  listOrgNodes,
  listRoles,
  listUsers,
  replaceRolePermissions,
} from './admin'

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
      json: async () => ({ code: 0, data: [{ code: 'SYSU', name: '中山大学', parentCode: null, hasChildren: true }] }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const nodes = await listOrgNodes()

    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/org-nodes')
    expect(nodes[0].code).toBe('SYSU')
  })

  it('listOrgNodeRoots calls GET /api/admin/org-nodes/roots', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ code: 0, data: [{ code: 'SYSU', name: '中山大学', parentCode: null, hasChildren: true }] }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const roots = await listOrgNodeRoots()

    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/org-nodes/roots')
    expect(roots[0].code).toBe('SYSU')
  })

  it('listOrgNodeChildren calls GET with parentCode query', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ code: 0, data: [{ code: 'CAT-party', name: '党群部门', parentCode: 'SYSU', hasChildren: true }] }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const children = await listOrgNodeChildren('SYSU')

    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/org-nodes/children')
    expect(fetchMock.mock.calls[0][0]).toContain('parentCode=SYSU')
    expect(children[0].code).toBe('CAT-party')
  })

  it('createOrgNode posts payload to /api/admin/org-nodes', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        code: 0,
        data: { code: 'NEW-DEPT', name: '新部门', parentCode: 'SYSU', hasChildren: false },
      }),
    })
    vi.stubGlobal('fetch', fetchMock)

    await createOrgNode({ code: 'NEW-DEPT', name: '新部门', parentCode: 'SYSU' })

    const init = fetchMock.mock.calls[0][1] as RequestInit
    expect(init.method).toBe('POST')
    expect(JSON.parse(init.body as string)).toMatchObject({ code: 'NEW-DEPT', parentCode: 'SYSU' })
  })

  it('canWriteDepartments checks admin:departments:write permission', () => {
    expect(canWriteDepartments(['admin:departments:read'])).toBe(false)
    expect(canWriteDepartments(['admin:departments:write'])).toBe(true)
  })

  it('canWriteUsers checks admin:users:write permission', () => {
    expect(canWriteUsers(['admin:users:read'])).toBe(false)
    expect(canWriteUsers(['admin:users:write'])).toBe(true)
  })

  it('listRoles calls GET /api/admin/roles', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        code: 0,
        data: [{ roleCode: 'ADMIN', name: '系统管理员', description: null, permissionCodes: ['home:read'], systemRole: true, permissionsEditable: false }],
      }),
    })
    vi.stubGlobal('fetch', fetchMock)

    const roles = await listRoles()

    expect(fetchMock.mock.calls[0][0]).toContain('/api/admin/roles')
    expect(roles[0].roleCode).toBe('ADMIN')
  })

  it('replaceRolePermissions puts permission payload', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        code: 0,
        data: { roleCode: 'GOVERNANCE', name: '治理岗', description: null, permissionCodes: ['home:read'], systemRole: false, permissionsEditable: true },
      }),
    })
    vi.stubGlobal('fetch', fetchMock)

    await replaceRolePermissions('GOVERNANCE', ['home:read'])

    const init = fetchMock.mock.calls[0][1] as RequestInit
    expect(init.method).toBe('PUT')
    expect(JSON.parse(init.body as string)).toEqual({ permissionCodes: ['home:read'] })
  })
})
