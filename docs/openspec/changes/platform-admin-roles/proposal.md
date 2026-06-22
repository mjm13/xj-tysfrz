## Why

007 已建立「平台管理」壳层与平台用户 UI，但 RBAC 主数据（`platform_role` / `platform_permission`）仍只能改库。管理员无法在 UI 中维护角色及其 Permission 集合，也无法为后续用户绑角色做准备。

## What Changes

- Flyway V4：新增 `admin:roles:read` / `admin:roles:write`，授予 ADMIN 角色
- 后端 Admin API：角色列表/创建/更新、Permission 目录、角色-权限绑定
- 前端 `/admin/roles`：角色列表 + 权限勾选编辑
- 扩展「平台管理」导航：顶栏与侧栏增加「角色管理」
- 权限路由：`/admin/roles` 需 `admin:roles:read`

**不在本 change**：用户-角色分配（007 扩展）、角色删除、菜单/部门管理（009–010）

## Capabilities

### Modified Capabilities

- `identity-access`：角色与 Permission 管理 API + UI；RoleCatalog 管理场景
- `platform-shell`：平台管理子菜单扩展「角色管理」；多 admin 子权限下的导航可见性

## Impact

| 区域 | 影响 |
| --- | --- |
| `db/migration` | V4 新增 admin:roles 权限 |
| `backend/access` | RoleAdminController、Permission 查询、RolePermission 绑定 |
| `frontend` | AdminRolesView、admin API 扩展、permissions 多路径映射 |
| `docs/requirements/inbox/008-*.md` | in-change |

## 来源需求

- `docs/requirements/inbox/008-platform-admin-roles.md`

## Change 类型

**业务 change**（identity-access RoleCatalog 管理能力）

## In Scope

- 角色 CRUD（不含删除有用户挂靠的角色；系统内置 ADMIN 不可删）
- Permission 全量目录只读 API
- 角色-Permission 绑定（全量替换）
- `/admin/roles` 前端页 + 测试
- 变更后权限生效策略：**需重新登录**（session 内 OperatorContext 不变）

## Out of Scope

- 平台用户绑/解绑角色（后续 007 扩展切片）
- 角色删除 API（Deferred，本切片仅禁删系统角色与有用户角色）
- 菜单 DB 化、部门管理

## Open Questions & Deferred

- ADMIN 角色 Permission 是否允许 UI 编辑？→ 本切片 **只读展示**，不可改（防锁死）
- 删除空角色 API → Deferred
- 在线刷新 session 权限 → Deferred（本期重新登录）
