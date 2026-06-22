## 1. 对齐与门禁

- [x] 1.1 阅读 `007-platform-admin-users.md` 与 proposal In Scope
- [x] 1.2 确认 008–010 需求已在 inbox，本 change 不实现

## 2. 后端 — org_node 只读 API

- [x] 2.1 `OrgNodeAdminController` GET `/api/admin/org-nodes` + `@SaCheckPermission("admin:users:read")`
- [x] 2.2 DTO 与 Mapper 查询（code, name, parentCode）
- [x] 2.3 集成测试：admin 200 / dept_admin 403

## 3. 后端 — 用户 API 测试补齐

- [x] 3.1 集成测试：GET/POST `/api/admin/users`  happy path + 403
- [x] 3.2 `mvn -pl xj-tysfrz-business -am test` 全绿

## 4. 前端 — 路由与权限

- [x] 4.1 `permissions.ts` 增加 `/admin/users` → platform-admin 映射；admin 路径权限校验
- [x] 4.2 `router` 增加 `/admin` ModuleLayout + `/admin/users`
- [x] 4.3 `module-nav.ts` 增加 `platform-admin` 侧栏（平台用户）
- [x] 4.4 `AppTopbar` 增加「平台管理」dropdown（首期仅平台用户）
- [x] 4.5 router/auth 测试：无 admin 权限不可见/不可进

## 5. 前端 — 平台用户页

- [x] 5.1 `api/admin.ts` listUsers、createUser、listOrgNodes
- [x] 5.2 `AdminUsersView.vue` 列表 + 创建对话框
- [x] 5.3 Vitest：API client mock；可选组件 smoke
- [x] 5.4 `npm run test` + `npm run build` 通过

## 6. 文档与索引

- [x] 6.1 更新 `docs/capability-map.md` 增加 platform-admin 行
- [x] 6.2 007 需求 AC 勾选 + 人工验收说明（apply 收尾）

## 7. 归档链（apply 完成后）

- [x] 7.1 `/opsx:sync`
- [x] 7.2 `/opsx:archive`
- [x] 7.3 `/opsx:sync-knowledge`
- [x] 7.4 007 `status: shipped` → `docs/requirements/shipped/`

## 8. 明确不在本 change

- 008 角色管理、009 部门管理、010 菜单 DB 化
- 用户编辑/禁用/角色分配
