## ADDED Requirements

### Requirement: 组织节点平台管理 API

系统 SHALL 提供平台管理侧 org_node 维护 API（懒加载树 + 创建/更新），受 `admin:departments:*` 保护。

#### Scenario: 查询根节点

- **WHEN** 持有 admin:departments:read 的用户 GET `/api/admin/org-nodes/roots`
- **THEN** MUST 返回 parent_code 为空的 org 节点列表

#### Scenario: 懒加载子节点

- **WHEN** 持有 admin:departments:read 的用户 GET `/api/admin/org-nodes/children?parentCode=SYSU`
- **THEN** MUST 返回 parent_code=SYSU 的直接子节点

#### Scenario: 创建组织节点

- **WHEN** 持有 admin:departments:write 的用户 POST 合法 code/name/parentCode
- **THEN** MUST 持久化 org_node；parent MUST 存在；level MUST 正确派生

#### Scenario: 更新组织节点

- **WHEN** 持有 admin:departments:write 的用户 PUT 更新 name 或 parentCode
- **THEN** MUST 持久化；parent 变更 MUST NOT 成环

#### Scenario: 无效 parent 拒绝

- **WHEN** parentCode 不存在或会导致环
- **THEN** MUST 返回业务错误并拒绝保存

#### Scenario: 无权限拒绝

- **WHEN** 无 admin:departments:read 的用户请求 roots/children
- **THEN** MUST 返回 403

### Requirement: 组织节点管理界面

前端 SHALL 提供 `/admin/departments`：ElTree 懒加载展示 org_node，支持新建与编辑（无删除）。

#### Scenario: 新建后用户表单可见

- **WHEN** 管理员创建新 org 节点且平台用户页拉取 org 列表
- **THEN** 新 code MUST 出现在部门选项中
