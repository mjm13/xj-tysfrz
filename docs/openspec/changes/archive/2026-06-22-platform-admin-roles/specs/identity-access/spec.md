## ADDED Requirements

### Requirement: 角色管理权限种子

系统 SHALL 通过 Flyway 提供 `admin:roles:read` 与 `admin:roles:write` Permission，并授予 ADMIN 角色。

#### Scenario: 管理员具备角色管理权限

- **WHEN** 数据库迁移至 V4 完成
- **THEN** ADMIN 用户 MUST 拥有 admin:roles:read 与 admin:roles:write

### Requirement: 角色管理 API

系统 SHALL 提供受 RBAC 保护的角色管理 API，管理 RoleCatalog（`platform_role` + `platform_role_permission`）。

#### Scenario: 列表角色

- **WHEN** 持有 `admin:roles:read` 的用户请求 GET `/api/admin/roles`
- **THEN** MUST 返回角色列表（roleCode、name、description、permissionCodes）

#### Scenario: 创建角色

- **WHEN** 持有 `admin:roles:write` 的用户 POST 合法 roleCode/name
- **THEN** MUST 创建角色；roleCode MUST 唯一且 MUST NOT 为 `ADMIN`

#### Scenario: 更新角色权限

- **WHEN** 持有 `admin:roles:write` 的用户 PUT `/api/admin/roles/{roleCode}/permissions` 含合法 permissionCodes
- **THEN** MUST 全量替换该角色的 Permission 关联

#### Scenario: 禁止修改 ADMIN 角色权限

- **WHEN** 请求 PUT `/api/admin/roles/ADMIN/permissions`
- **THEN** MUST 返回 403 或业务拒绝

#### Scenario: 无权限拒绝

- **WHEN** 无 `admin:roles:read` 的用户请求角色 API
- **THEN** MUST 返回 403

### Requirement: Permission 目录 API

系统 SHALL 提供 GET `/api/admin/permissions`，返回全部 Permission（permissionCode、moduleName、actionName），供角色编辑 UI 使用；需 `admin:roles:read`。

#### Scenario: 拉取 Permission 目录

- **WHEN** 管理员请求 GET `/api/admin/permissions`
- **THEN** MUST 返回完整 Permission 列表

### Requirement: 角色管理界面

前端 SHALL 提供 `/admin/roles` 页面：展示角色列表，支持创建角色与为非 ADMIN 角色编辑 Permission 多选集。

#### Scenario: 编辑 GOVERNANCE 角色权限

- **WHEN** 管理员为 GOVERNANCE 勾选/取消 Permission 并保存
- **THEN** API 成功且列表反映新 permissionCodes

#### Scenario: ADMIN 角色只读

- **WHEN** 管理员查看 ADMIN 角色
- **THEN** UI MUST NOT 提供保存权限操作（或后端拒绝）

### Requirement: 角色权限变更生效策略

角色 Permission 变更后，**已登录用户**的 session permissions MUST NOT 自动变更；用户 MUST 重新登录后新 Permission 才生效。

#### Scenario: 变更后需重新登录

- **WHEN** 管理员修改某角色 Permission 且该角色用户已在线
- **THEN** 该用户当前 session permissions MUST 保持变更前集合，直至重新登录
