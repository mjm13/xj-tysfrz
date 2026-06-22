## ADDED Requirements

### Requirement: 平台管理部门子菜单

「平台管理」SHALL 提供「部门管理」→ `/admin/departments`；需 `admin:departments:read`。

#### Scenario: 有部门读权限可见

- **WHEN** 用户 permissions 含 `admin:departments:read`
- **THEN** MUST 显示「部门管理」入口

### Requirement: 部门管理路由守卫

无 `admin:departments:read` 访问 `/admin/departments` MUST 被拒绝。

#### Scenario: 越权 URL

- **WHEN** 无 admin:departments:read 的用户访问 `/admin/departments`
- **THEN** MUST 重定向至 `/` 或等效拒绝
