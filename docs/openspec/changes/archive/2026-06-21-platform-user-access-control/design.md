# Design: platform-user-access-control

> **业务 change**：领域设计 + 全栈实现（后端 auth + 前端对接 + 最小 org 种子）。

## Context

- 前端：`identity-access-mock`（Pinia 本地校验，`stores/auth.ts`）；路由仅检查 `isAuthenticated`
- 后端：无 Spring Security；仅有 Ping / SystemInfo；Flyway baseline 存在
- 领域：005 已定义 8 上下文；`identity-access` 在 context-map 中为横切 ACL，尚未落地
- org-structure：模型已 established，表/API 未实现；E1 已确认本 change 内含最小 `org_node` 种子

## Goals / Non-Goals

**Goals:**

- 替换 Mock 为真实 JWT 认证 + RBAC + DataScope
- 提供 `OperatorContext` 供后续 API 横切使用
- `DataScopeResolver` 基于 org_node 树派生 `ScopedDeptSet`
- Flyway seed：admin 用户 + GLOBAL + 全模块权限 + SYSU_ORG 组织种子
- 前端登录/守卫/导航对接真实 API

**Non-Goals:**

- SSO（CAS/OIDC/LDAP）实际对接——仅 `AuthProvider` 扩展点
- `ServiceAccount` / 接入服务账号
- 用户管理 UI 完整 CRUD 页面（API 优先，管理 UI 可最小化）
- org-structure 完整功能（OrgMapping、OrgRoster、树编辑）
- 各业务模块的数据范围过滤实现（仅提供 Resolver + 契约 + 示例 endpoint 测试）

## 领域设计

详见 [`docs/domain/developing/`](../../../docs/domain/developing/) 三件套。

核心聚合：`InteractiveUser`、`Role`/`Permission`；领域服务：`DataScopeResolver`、`AuthProvider`。

### 与 PersonUID 的边界

```
InteractiveUser.platformUserId  ≠  PersonUID
DepartmentRef → OrgNode.code     →  操作者归属（非自然人身份）
```

## Decisions

### D1: 认证与会话 — JWT 无状态

- **选择：** HS256 JWT，claims 含 sub、roles、permissions、dataScope、deptCode；默认 TTL 8h
- **理由：** 无状态、与 Spring Security 集成成熟；SSO 后续走同一 Token 签发接口
- **备选：** 服务端 Session（需 Redis，本期过度）

### D2: 密码存储 — BCrypt

- **选择：** Spring Security `BCryptPasswordEncoder`
- **理由：** 行业标准；无需自管 salt

### D3: 权限码 — module:action

- **选择：** `identity-basic:read`、`data-query:execute` 等；首期路由/API enforce 到 module 级，action 细粒度预留
- **理由：** 与 ModuleLayout 模块 key 对齐；可渐进细化

### D4: DataScope 三档 + 组织树派生

- **选择：** `GLOBAL` / `OWN_DEPT` / `OWN_DEPT_AND_SUB`；`DataScopeResolver` 读 org_node 表 BFS 子孙
- **理由：** 需求已锁定；实时派生，组织变更无需改用户表

### D5: E1 最小 org_node 种子

- **选择：** Flyway `V00x__org_node_seed.sql` 从 `frontend/src/mocks/sysu-org.ts` 对应数据导入（或构建脚本生成 SQL）
- **理由：** DepartmentRef 外键语义；完整 org-structure change 后续扩展 OrgMapping/Roster
- **备选：** 等 org-structure change 先做（阻塞 006 过久）

### D6: OperatorContext 注入

- **选择：** Spring Filter 解析 JWT → 构建 `OperatorContext` → 存入 `SecurityContext` / `@CurrentOperator` 参数解析器
- **理由：** 横切一致；业务 Service 调用 `operator.scopedDeptCodes()`

### D7: AuthProvider 可插拔

- **选择：** 接口 `AuthProvider`；本期 `SelfBuiltAuthProvider`；预留 `SsoAuthProvider` bean 位
- **理由：** 需求「自建 + 预留 SSO」；切换不改变 RBAC/DataScope

### D8: 分层裁剪

- **选择：** identity-access 采用 **完整 DDD 分层**（domain / application / infrastructure / interfaces）
- **理由：** RBAC + DataScope 有不变量与领域服务；非简单 CRUD

## 技术方案

### 后端包结构（建议）

```
com.xj.zbpt.access/
  domain/          InteractiveUser, Role, Permission, DataScope, OperatorContext
  domain/service/  DataScopeResolver, AuthProvider
  application/     AuthAppService, UserAdminAppService
  infrastructure/  JwtTokenService, UserRepository, OrgNodeRepository
  interfaces/      AuthController, UserController, CurrentOperatorResolver
  config/          SecurityConfig
```

### Flyway 表（摘要）

| 表 | 用途 |
| --- | --- |
| org_node | code, name, parent_code, level（种子） |
| platform_user | 用户主表 |
| role, permission | 角色权限 |
| user_role, role_permission | 关联 |
| （无 scoped_dept 持久化） | 运行时派生 |

### API（摘要）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 用户名密码 → JWT |
| POST | `/api/auth/logout` | 客户端清 Token（可选黑名单后续） |
| GET | `/api/auth/me` | 当前用户 profile + permissions |
| GET/POST/PUT | `/api/admin/users` | 用户管理（需 admin 权限） |
| GET | `/api/admin/roles` | 角色列表 |

### 前端变更

- `stores/auth.ts`：login 调 API，存 token + profile
- `router/index.ts`：beforeEach 校验 token；meta.permission 可选
- `AppTopbar` / `module-nav`：按 permissions 过滤
- 删除或隔离 `auth-utils.ts` 中 Mock 校验

### DataScope 消费契约（供后续 change）

```java
// 业务 Service 示例
void listDataSources(OperatorContext op) {
  if (!op.isGlobal()) {
    query.in("dept_code", op.scopedDeptCodes());
  }
}
```

本 change 提供 **演示 endpoint**（如 `/api/demo/scoped-depts`）+ 集成测试验证 Resolver。

## Risks / Trade-offs

| 风险 | 缓解 |
| --- | --- |
| org_node 种子与后续 org-structure 重复 | 表结构按 established spec 设计；org-structure change 扩展而非重写 |
| JWT 无 revoke | 短 TTL + 后续 refresh/blacklist；本期可接受 |
| 权限与 module-nav 不同步 | 权限码与 moduleKey 映射表在 design 中维护一份常量 |
| Spring Security 学习曲线 | 从最小配置开始；Controller 测试覆盖 login/403 |

## Migration Plan

1. 部署 Flyway 迁移 + seed（admin 默认密码文档化，首次登录强制改密可后续）
2. 后端 auth API 上线
3. 前端切换真实登录；移除 Mock 路径
4. 验证：未授权 403、DataScope 过滤演示 endpoint

**Rollback：** 前端可临时回退 Mock branch；DB 迁移需 down 脚本或备份

## Open Questions

- （无阻塞）用户管理 UI 深度：apply 阶段默认 API + 最小 admin 页或仅 API + seed
