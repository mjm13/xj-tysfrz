# ADR 0008: 平台用户与访问控制关键决策

- **Status:** accepted
- **Date:** 2026-06-21
- **Change:** platform-user-access-control
- **Related:** ADR 0007

## Context

006 需求落地真实 `identity-access`，替换 Mock 登录，提供 RBAC + 部门 DataScope。E1 已确认 org-structure 表未落地时由本 change 导入最小 `org_node` 种子。

## Decision

### D1 — JWT 无状态会话

- HS256 JWT，claims 含 sub、roles、permissions、dataScope、deptCode、scopedDeptCodes
- 默认 TTL 8h

### D2 — BCrypt 密码哈希

- Spring Security `BCryptPasswordEncoder`

### D3 — 权限码 module:action

- 如 `identity-basic:read`；与 ModuleLayout moduleKey 对齐

### D4 — DataScope 三档

- `GLOBAL` / `OWN_DEPT` / `OWN_DEPT_AND_SUB`
- `DataScopeResolver` 基于 org_node 树 BFS 派生 ScopedDeptSet

### D5 — E1 最小 org_node 种子

- Flyway V2 从 SYSU_ORG mock 导入 790 节点
- 完整 org-structure 留给后续 change

### D6 — OperatorContext 注入

- JwtAuthenticationFilter → SecurityContext
- `@CurrentOperator` 参数解析器

### D7 — AuthProvider 可插拔

- 本期 `SelfBuiltAuthProvider`；SSO 预留

### D8 — 初始管理员

- Flyway seed：`admin` / `admin123`，GLOBAL DataScope，全模块 read 权限

## Consequences

- `identity-access-mock` 前端路径退役
- 后续业务 API 须读取 OperatorContext 施加 DataScope
- JWT secret 生产环境 MUST 通过环境变量 `JWT_SECRET` 配置

## Alternatives considered

- 服务端 Session + Redis：本期过度
- 独立行政部门树：与 006 决策（复用 OrgNode）冲突，拒绝
