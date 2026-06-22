## ADDED Requirements

### Requirement: 平台管理顶栏入口

系统 SHALL 在顶栏提供「平台管理」导航入口；仅当用户持有 `admin:users:read`（或后续 admin 子权限）时 MUST 显示。

#### Scenario: 管理员可见平台管理

- **WHEN** 用户 permissions 含 `admin:users:read`
- **THEN** 顶栏 MUST 显示「平台管理」且可进入「平台用户」

#### Scenario: 无权限不可见

- **WHEN** 用户 permissions 不含任何 admin 用户读权限
- **THEN** 顶栏 MUST NOT 显示「平台管理」

### Requirement: 平台管理模块布局

`/admin/users` SHALL 使用 ModuleLayout，moduleKey 为 `platform-admin`，侧栏展示平台管理子菜单（首期至少「平台用户」）。

#### Scenario: 平台用户页布局

- **WHEN** 用户访问 `/admin/users`
- **THEN** 页面 MUST 使用 ModuleLayout 与 platform-admin 侧栏配置

### Requirement: 平台管理路由守卫

无 `admin:users:read` 的用户 direct 访问 `/admin/*` MUST 被路由守卫拒绝（重定向首页或 403 页）。

#### Scenario: 越权 URL

- **WHEN** 无 admin 权限用户访问 `/admin/users`
- **THEN** 系统 MUST 重定向至 `/` 或等效拒绝
