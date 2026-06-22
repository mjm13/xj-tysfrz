# platform-shell Specification

## Purpose

平台壳层与 identity-access 集成：真实登录 API 对接、Token 路由守卫、按权限过滤导航。

## ADDED Requirements

### Requirement: 后端认证集成

登录页 SHALL 调用 identity-access 后端登录 API 获取 JWT；MUST NOT 使用前端 Mock 本地校验作为生产路径。

#### Scenario: 登录成功导航

- **WHEN** 用户在登录页输入有效凭证并提交
- **THEN** 系统 MUST 调用 POST `/api/auth/login`，存储 AccessToken，并导航至 `/`

#### Scenario: 登录失败提示

- **WHEN** 后端返回 401
- **THEN** 登录页 MUST 显示错误提示且 MUST NOT 建立会话

### Requirement: Token 路由守卫

路由守卫 SHALL 依据有效 JWT（或 /api/auth/me 校验）保护非 public 路由；无效或缺失 Token MUST 重定向至 `/login`。

#### Scenario: 未登录访问受保护路由

- **WHEN** 无有效 Token 的用户访问 `/` 或业务模块路由
- **THEN** 系统 MUST 重定向至 `/login`

#### Scenario: 已登录访问登录页

- **WHEN** 已持有有效 Token 的用户访问 `/login`
- **THEN** 系统 MUST 重定向至 `/`

### Requirement: 按权限过滤导航

顶栏与模块导航 SHALL 根据当前用户 permissions 隐藏或禁用未授权模块入口。

#### Scenario: 无权限模块不可见

- **WHEN** 用户无某 module 的 read 权限
- **THEN** 对应导航入口 MUST 不可见或 disabled，且 direct URL 访问 MUST 被守卫或后端 403 拦截

### Requirement: 会话持久化 Token

前端 SHALL 持久化 AccessToken（localStorage 或 sessionStorage）；刷新页面后 MUST 恢复登录态（Token 未过期时）。

#### Scenario: 刷新保持登录

- **WHEN** 用户已登录且 Token 未过期并刷新页面
- **THEN** 会话 MUST 从存储恢复，用户仍处于已登录状态

#### Scenario: 不存储密码

- **WHEN** 写入持久化存储
- **THEN** 系统 MUST NOT 存储明文密码

## REMOVED Requirements

（本 change 无 platform-shell 主 spec 移除项；Mock 认证要求见 identity-access-mock delta。）
