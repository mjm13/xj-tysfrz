# identity-access-mock Specification

## Purpose

Mock 身份与访问能力——由 identity-access 真实实现替代。

## REMOVED Requirements

### Requirement: Mock 登录

**Reason**: platform-user-access-control 落地真实 identity-access（JWT + 后端 API），Mock 本地校验不再满足安全与 RBAC 需求。

**Migration**: 前端 `stores/auth.ts` 改为调用 `/api/auth/login`；删除 `validateMockCredentials` 生产路径。Spec 能力迁移至 `identity-access` 与 `platform-shell` delta。

### Requirement: 路由守卫

**Reason**: 守卫逻辑保留但认证依据从 Mock Session 改为 JWT Token（platform-shell ADDED）。

**Migration**: `router/index.ts` 校验 Token 有效性而非仅 `isAuthenticated` mock 标志。

### Requirement: 会话持久化抽象

**Reason**: 会话存储改为持久化 AccessToken + profile（platform-shell ADDED）。

**Migration**: SessionStore 存 token 与 user profile，不再存 mock 布尔标志 alone。

### Requirement: 退出登录

**Reason**: 退出改为清除 Token 并可选调用 logout API（identity-access）。

**Migration**: logout action 清除 token storage 并导航 `/login`。
