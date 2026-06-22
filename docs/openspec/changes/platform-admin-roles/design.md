## Context

- 表：`platform_role`、`platform_permission`、`platform_role_permission` 已存在（V3 seed：ADMIN、GOVERNANCE）
- 实体：`RoleEntity`、`RoleMapper`；Permission 无独立 Entity，可通过 SQL 或新增 `PermissionEntity`
- 007 模式：`UserAdminController` + AppService + `@SaCheckPermission`
- Sa-Token 权限来自登录时写入 session 的 `OperatorContext`；改 role_permission 后**已登录用户 permissions 不变**，直到重新登录

## Goals / Non-Goals

**Goals**

- 管理员在 UI 查看/创建/编辑（非系统）角色，并为角色分配 Permission
- ADMIN 角色在 UI 可查看但不可编辑权限

**Non-Goals**

- 用户-角色分配
- 角色删除
- Permission 新增（Permission 仍由 Flyway/种子维护，非运行时 CRUD）

## 领域设计（identity-access / RoleCatalog）

- 管理对象：**Role**、**Permission**（只读目录）、**RolePermission** 关联
- 不变量：
  - `role_code` 唯一
  - Permission 格式 `module:action`
  - 系统角色 `ADMIN` MUST NOT 删除或降级 admin 自管权限（本切片：UI 禁止编辑 ADMIN 权限集）
  - 有 `platform_user_role` 挂靠的角色 MUST NOT 删除（本切片不提供 delete）

## 技术方案

### Flyway V4

```sql
INSERT platform_permission (13, 'admin:roles:read', ...), (14, 'admin:roles:write', ...);
INSERT platform_role_permission (role_id=1 ADMIN, permission 13,14);
```

同步 `migration-test/V4__admin_roles_permission.sql`。

### API

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/api/admin/roles` | admin:roles:read | 角色列表 + permissionCodes |
| POST | `/api/admin/roles` | admin:roles:write | 创建角色 `{roleCode,name,description?}` |
| PUT | `/api/admin/roles/{roleCode}` | admin:roles:write | 更新 name/description（非 ADMIN 或允许 ADMIN 仅改描述） |
| PUT | `/api/admin/roles/{roleCode}/permissions` | admin:roles:write | body: `{permissionCodes:[]}` 全量替换 |
| GET | `/api/admin/permissions` | admin:roles:read | 全量 Permission 目录 |

**保护规则**

- `roleCode=ADMIN`：PUT permissions → 403
- 创建时 roleCode 不可与已有重复；不可使用保留码 `ADMIN`（创建新 ADMIN 禁止）

### 前端

- 路由 `/admin/roles`，moduleKey `platform-admin`
- `permissions.ts`：
  - `/admin/roles` → 需 `admin:roles:read`
  - `platform-admin` 模块可见：`admin:users:read` **或** `admin:roles:read`
- `AdminRolesView.vue`：角色表格 + 编辑抽屉（Permission 多选）
- `AppTopbar` / `module-nav` 增加「角色管理」

### 测试

- `RoleAdminIntegrationTest`：list/create/update permissions + ADMIN 保护 + 403
- `permissions.test.ts`：roles 路径与 platform-admin 可见性
- `admin.test.ts`：roles API client

## 分层裁剪

Controller → RoleAdminAppService → RoleMapper + 自定义 RolePermission 写入

## Open Questions

- 用户绑角色：独立切片，不在本 change
