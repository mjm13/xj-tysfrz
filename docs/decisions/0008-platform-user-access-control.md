# ADR 0008: 平台用户与访问控制关键决策

- **Status:** accepted（2026-06-22 修订：D1/D2/D6 会话与鉴权栈改为 Sa-Token）
- **Date:** 2026-06-21（初版）；2026-06-22（Sa-Token 修订）
- **Change:** platform-user-access-control
- **Related:** ADR 0007

## 背景（Context）

006 需求落地真实 `identity-access`，替换 Mock 登录，提供 RBAC + 部门 DataScope。E1 已确认 org-structure 表未落地时由本 change 导入最小 `org_node` 种子。

2026-06-22 将鉴权栈从 Spring Security + 自研 JWT 全量替换为 **Sa-Token**：横切能力下沉 `xj-zbpt-framework`，`xj-zbpt-business` 仅做业务接入；旧 JWT **不兼容**，切换后须重新登录。

## 决策（Decision）

### D1 — Sa-Token 会话（替代 JWT）

- 使用 **Sa-Token 1.44.x**（`sa-token-spring-boot3-starter`）
- Token 经 `Authorization: Bearer <token>` 传递（`token-name: Authorization`，`token-prefix: Bearer`）
- 默认 TTL **8h**（`sa-token.timeout: 28800`）
- 登录：`StpUtil.login(platformUserId)`；`OperatorContext` 写入 Sa-Token Session
- **不**再签发/解析 JWT；不兼容旧 Token

### D2 — BCrypt 密码哈希

- `spring-security-crypto` 的 `BCryptPasswordEncoder`（**不**引入完整 Spring Security 鉴权链）

### D3 — 权限码 module:action

- 如 `identity-basic:read`；与 ModuleLayout moduleKey 对齐
- 方法级鉴权：`@SaCheckPermission`；权限列表由 `StpInterfaceImpl` 从 Session 中的 `OperatorContext` 读取

### D4 — DataScope 三档

- `GLOBAL` / `OWN_DEPT` / `OWN_DEPT_AND_SUB`
- `DataScopeResolver` 基于 org_node 树 BFS 派生 ScopedDeptSet

### D5 — E1 最小 org_node 种子

- Flyway V2 从 SYSU_ORG mock 导入 790 节点
- 完整 org-structure 留给后续 change

### D6 — OperatorContext 注入（framework 层）

- **`xj-zbpt-framework`**：`SaInterceptor` 全局登录校验 + 白名单；`OperatorSessionSupport` 读写 Session；`@CurrentOperator` + `CurrentOperatorArgumentResolver`
- **`xj-zbpt-common`**：`OperatorContext`、`DataScope` 共享模型
- **`xj-zbpt-business`**：`AuthAppService` 登录/登出调用 Sa-Token；Controller 使用 `@SaCheckPermission` / `@CurrentOperator`
- 401/403：`NotLoginException` / `NotPermissionException` → `GlobalExceptionHandler`

### D7 — AuthProvider 可插拔

- 本期 `SelfBuiltAuthProvider`；SSO 预留

### D8 — 初始管理员

- Flyway seed：`admin` / `admin123`，GLOBAL DataScope，全模块 read 权限

## 影响（Consequences）

- `identity-access-mock` 前端路径退役
- 后续业务 API 须读取 OperatorContext 施加 DataScope
- 生产环境可通过 `SA_TOKEN_TIMEOUT` 调整会话 TTL；分布式会话/Redis 留给后续 change
- 前端契约不变：`login` 仍返回 `accessToken` 字段（值为 Sa-Token token）

## 备选方案（Alternatives considered）

- **JWT 无状态（初版 D1，已废弃）**：与 Sa-Token 统一会话/权限注解栈冲突；2026-06-22 全量替换
- 服务端 Session + Redis：Sa-Token 可扩展；本期默认内存 Session
- 独立行政部门树：与 006 决策（复用 OrgNode）冲突，拒绝
- Spring Security 鉴权链 + Sa-Token 共存：复杂度高，拒绝；仅保留 crypto 做 BCrypt
