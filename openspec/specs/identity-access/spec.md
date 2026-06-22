# identity-access Specification

## Purpose

平台访问控制（identity-access）：平台操作者（InteractiveUser）的认证、RBAC、部门数据范围（DataScope）与请求级 OperatorContext。与 identity-master 的 PersonUID **无关**。

## Requirements

### Requirement: 平台用户与自然人分离

系统 SHALL 使用 PlatformUserId 标识平台操作者账号；MUST NOT 将 PersonUID 用作登录身份。

#### Scenario: 用户标识独立

- **WHEN** 创建 InteractiveUser
- **THEN** 系统 MUST 分配 PlatformUserId，且 MUST NOT 要求绑定 PersonUID

### Requirement: 自建账号认证

系统 SHALL 提供自建用户名/密码认证（AuthSource = SELF_BUILT）；密码 MUST 以加盐哈希存储，MUST NOT 明文持久化。

#### Scenario: 有效凭证登录

- **WHEN** 用户提交正确的用户名与密码
- **THEN** 系统 MUST 签发 AccessToken（JWT）并返回用户 profile（含 roles、permissions、dataScope、departmentRef）

#### Scenario: 无效凭证拒绝

- **WHEN** 用户提交错误密码或不存在用户名
- **THEN** 系统 MUST 返回 401，MUST NOT 签发 Token

#### Scenario: 禁用用户拒绝

- **WHEN** 用户 status 为 DISABLED
- **THEN** 系统 MUST 拒绝登录

### Requirement: JWT 访问控制

系统 SHALL 通过 JWT 保护受保护 API；无有效 Token 或 Token 过期 MUST 返回 401。

#### Scenario: 受保护 API 无 Token

- **WHEN** 请求受保护 API 且未携带 Authorization Bearer Token
- **THEN** 系统 MUST 返回 401

#### Scenario: Token 过期

- **WHEN** 请求携带已过期 JWT
- **THEN** 系统 MUST 返回 401

### Requirement: RBAC 最小权限

系统 SHALL 基于 Role 与 Permission（格式 `module:action`）控制功能访问；未显式授予的权限 MUST 默认拒绝。

#### Scenario: 无权限访问模块

- **WHEN** 用户无某 module 的 read 权限
- **THEN** 系统 MUST 返回 403

#### Scenario: 角色授权生效

- **WHEN** 用户被分配含 `identity-basic:read` 的角色且已登录
- **THEN** 访问 identity-basic 相关 API MUST 允许

### Requirement: 部门归属引用 OrgNode

用户的 DepartmentRef MUST 引用 org-structure 中存在的 OrgNode.code；设置不存在的 code MUST 拒绝。

#### Scenario: 有效部门归属

- **WHEN** 管理员为用户设置 departmentRef 为 org_node 表中存在的 code
- **THEN** 系统 MUST 接受并持久化

#### Scenario: 无效部门归属

- **WHEN** 管理员设置 departmentRef 为不存在的 org code
- **THEN** 系统 MUST 返回校验错误并拒绝保存

### Requirement: DataScope 三档派生

系统 SHALL 支持 DataScope：`GLOBAL`、`OWN_DEPT`、`OWN_DEPT_AND_SUB`；ScopedDeptSet MUST 由 DataScopeResolver 依据 DepartmentRef 与 org_node 组织树实时派生。

#### Scenario: GLOBAL 全校口径

- **WHEN** 用户 dataScope 为 GLOBAL
- **THEN** OperatorContext MUST 标记为全局可见（查询层可跳过部门过滤）

#### Scenario: OWN_DEPT 本部门

- **WHEN** 用户 dataScope 为 OWN_DEPT 且 departmentRef 为 `DEPT_A`
- **THEN** ScopedDeptSet MUST 仅含 `{DEPT_A}`

#### Scenario: OWN_DEPT_AND_SUB 本部门及下级

- **WHEN** 用户 dataScope 为 OWN_DEPT_AND_SUB 且 departmentRef 为某 OrgNode
- **THEN** ScopedDeptSet MUST 含该节点及其组织树下全部子孙节点 code

### Requirement: 后端统一数据范围过滤

数据范围过滤 MUST 在后端 OperatorContext 统一施加；MUST NOT 仅依赖前端隐藏作为唯一安全控制。

#### Scenario: 绕过前端直接调 API

- **WHEN** 客户端直接调用受 DataScope 约束的演示/业务 API
- **THEN** 响应 MUST 按 OperatorContext.scopedDeptCodes 过滤，不得返回超范围数据

### Requirement: OperatorContext 请求注入

每个受保护 API 请求 MUST 可获取 OperatorContext（含 platformUserId、roles、permissions、dataScope、scopedDeptCodes）。

#### Scenario: 解析当前操作者

- **WHEN** 有效 JWT 请求到达受保护 API
- **THEN** 业务层 MUST 可读取完整 OperatorContext

### Requirement: AuthProvider 可插拔

系统 SHALL 定义 AuthProvider 抽象；切换 SELF_BUILT 与后续 SSO MUST NOT 改变 RBAC 与 DataScope 语义。

#### Scenario: 扩展点存在

- **WHEN** 查阅认证模块设计
- **THEN** MUST 存在 AuthProvider 接口与 SelfBuiltAuthProvider 实现；SSO 实现为预留扩展

### Requirement: Principal 两子类预留

领域模型 MUST 定义 Principal 抽象及 InteractiveUser、ServiceAccount 两子类；本期 MUST 仅实现 InteractiveUser。

#### Scenario: ServiceAccount 未实现

- **WHEN** 尝试使用 ServiceAccount 凭证登录
- **THEN** 系统 MUST NOT 提供该能力（404 或未实现），且 MUST NOT 影响 InteractiveUser 流程

### Requirement: 初始管理员种子

系统 SHALL 通过 Flyway seed 创建至少一名 GLOBAL DataScope 的管理员用户及基础角色权限，保障系统可首次登录管理。

#### Scenario: 首次部署可登录

- **WHEN** 数据库迁移完成且未创建其他用户
- **THEN** 管理员种子账号 MUST 可登录并拥有 GLOBAL 与全模块读权限
