## ADDED Requirements

### Requirement: 平台用户管理 API

系统 SHALL 提供受 RBAC 保护的平台用户管理 API；管理对象为 InteractiveUser（`platform_user`），MUST NOT 与 PersonUID 混淆。

#### Scenario: 列表平台用户

- **WHEN** 持有 `admin:users:read` 的用户请求 GET `/api/admin/users`
- **THEN** 系统 MUST 返回用户摘要列表（username、platformUserId、departmentCode、dataScope、status）

#### Scenario: 创建平台用户

- **WHEN** 持有 `admin:users:write` 的用户 POST 合法 CreateUserRequest
- **THEN** 系统 MUST 创建用户并返回摘要；密码 MUST BCrypt 存储

#### Scenario: 无读权限拒绝列表

- **WHEN** 无 `admin:users:read` 的用户请求 GET `/api/admin/users`
- **THEN** 系统 MUST 返回 403

### Requirement: 组织节点只读列表（管理 UI）

系统 SHALL 提供 GET `/api/admin/org-nodes`，返回 org_node 的 code/name/parentCode，供平台用户创建表单部门选择；需 `admin:users:read`。

#### Scenario: 管理员拉取部门选项

- **WHEN** 持有 `admin:users:read` 的用户请求 GET `/api/admin/org-nodes`
- **THEN** 系统 MUST 返回 org_node 列表（只读）

#### Scenario: 无权限拒绝

- **WHEN** 无 `admin:users:read` 的用户请求该 API
- **THEN** 系统 MUST 返回 403

### Requirement: 平台用户管理界面

前端 SHALL 提供 `/admin/users` 页面：展示平台用户列表并支持创建（username、password、departmentCode、dataScope）；MUST 调用上述 API，MUST NOT 使用 Mock 数据作为生产路径。

#### Scenario: 创建成功后刷新列表

- **WHEN** 管理员提交合法创建表单且 API 成功
- **THEN** 列表 MUST 包含新用户且 MUST NOT 在 localStorage 伪造用户

#### Scenario: 无效部门拒绝

- **WHEN** 创建请求含不存在的 departmentCode
- **THEN** 前端 MUST 展示后端错误信息
