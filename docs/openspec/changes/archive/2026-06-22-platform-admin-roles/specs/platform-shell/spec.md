## ADDED Requirements

### Requirement: 平台管理角色子菜单

「平台管理」模块 SHALL 在侧栏与顶栏 dropdown 提供「角色管理」入口 → `/admin/roles`；仅当用户持有 `admin:roles:read` 时可见。

#### Scenario: 有角色读权限可见入口

- **WHEN** 用户 permissions 含 `admin:roles:read`
- **THEN** MUST 显示「角色管理」且可导航至 `/admin/roles`

#### Scenario: 平台管理模块多权限可见

- **WHEN** 用户仅有 `admin:roles:read` 而无 `admin:users:read`
- **THEN** 顶栏 MUST 仍显示「平台管理」，且仅展示有权限的子菜单项

### Requirement: 角色管理路由守卫

无 `admin:roles:read` 的用户访问 `/admin/roles` MUST 被路由守卫拒绝。

#### Scenario: 越权访问角色页

- **WHEN** 无 `admin:roles:read` 的用户访问 `/admin/roles`
- **THEN** MUST 重定向至 `/` 或等效拒绝
