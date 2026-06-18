# identity-access-mock Specification

## Purpose

前端壳层阶段的 Mock 身份与访问能力：在不依赖后端的前提下，提供本地登录、路由守卫、会话持久化抽象与退出登录，为后续真实鉴权（Token）对接预留接口。

## Requirements

### Requirement: Mock 登录

系统 SHALL 在本 change 阶段使用 Mock 登录：不调用后端 API，前端本地校验凭证并建立会话（Session）。

#### Scenario: 非空凭证登录成功

- **WHEN** 用户在登录页输入非空的用户名与密码并点击登录
- **THEN** 系统设置已登录会话并导航至首页 `/`

#### Scenario: 空凭证拒绝

- **WHEN** 用户提交空的用户名或密码
- **THEN** 系统 MUST 阻止登录并提示校验错误

### Requirement: 路由守卫

系统 SHALL 通过路由守卫（AuthGuard）保护主布局路由，依据会话状态控制访问。

#### Scenario: 未登录访问受保护路由

- **WHEN** 未登录用户访问 `/` 或任意需认证路由
- **THEN** 系统 MUST 重定向至 `/login`

#### Scenario: 已登录访问登录页

- **WHEN** 已登录用户访问 `/login`
- **THEN** 系统 MUST 重定向至 `/`

### Requirement: 会话持久化抽象

系统 SHALL 通过 SessionStore（Pinia）管理认证态，并预留 localStorage/sessionStorage 持久化接口，供后续真实 Token 对接。

#### Scenario: 刷新后保持登录态

- **WHEN** 用户已 Mock 登录并刷新页面
- **THEN** 会话 MUST 从持久化存储恢复，用户仍处于已登录状态

#### Scenario: 不存储真实密钥

- **WHEN** Mock 阶段写入持久化存储
- **THEN** 系统 MUST NOT 存储真实密码或生产密钥

### Requirement: 退出登录

系统 SHALL 提供退出登录能力，清除会话并返回登录页。

#### Scenario: 退出成功

- **WHEN** 已登录用户触发退出登录
- **THEN** 系统清除 SessionStore 与持久化数据，并导航至 `/login`
