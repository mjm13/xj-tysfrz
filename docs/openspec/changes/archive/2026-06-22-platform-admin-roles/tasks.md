## 1. 对齐

- [x] 1.1 阅读 `008-platform-admin-roles.md` 与 proposal In Scope
- [x] 1.2 确认用户-角色分配、角色删除不在本 change

## 2. Flyway V4

- [x] 2.1 `V4__admin_roles_permission.sql`（prod + migration-test）
- [x] 2.2 插入 admin:roles:read/write 并授予 ADMIN

## 3. 后端 — 实体与 Mapper

- [x] 3.1 `PermissionEntity` + `PermissionMapper`（或 RoleMapper 扩展 SQL）
- [x] 3.2 `RolePermission` 写入/查询（全量替换）

## 4. 后端 — RoleAdmin API

- [x] 4.1 DTO：RoleSummaryDto、CreateRoleRequest、UpdateRoleRequest、ReplaceRolePermissionsRequest、PermissionDto
- [x] 4.2 `RoleAdminAppService` + `RoleAdminController`
- [x] 4.3 ADMIN 权限编辑保护、创建禁止 ADMIN roleCode
- [x] 4.4 `RoleAdminIntegrationTest` 全绿

## 5. 后端 — 验证

- [x] 5.1 `cd backend && .\mvnw.cmd -pl xj-tysfrz-business -am test` 全绿

## 6. 前端 — 路由与权限

- [x] 6.1 `permissions.ts`：`/admin/roles` + platform-admin 多权限可见
- [x] 6.2 router `/admin/roles`；module-nav + AppTopbar「角色管理」
- [x] 6.3 `permissions.test.ts` 更新

## 7. 前端 — 角色管理页

- [x] 7.1 `api/admin.ts` 扩展 roles/permissions API
- [x] 7.2 `AdminRolesView.vue`
- [x] 7.3 `admin.test.ts` 扩展
- [x] 7.4 `npm run test && npm run build`

## 8. 文档与收尾

- [x] 8.1 更新 `docs/capability-map.md`（platform-admin 能力描述）
- [x] 8.2 008 AC + 人工验收说明（apply 收尾）
- [x] 8.3 sync → archive → sync-knowledge → 008 shipped

## 9. 不在本 change

- 用户绑角色、角色删除、Permission 运行时 CRUD
