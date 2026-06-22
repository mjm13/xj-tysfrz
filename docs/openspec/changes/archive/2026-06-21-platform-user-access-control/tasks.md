## 1. 领域文档与需求对齐

- [x] 1.1 交叉核对 `docs/domain/developing/*` 与 `openspec/changes/platform-user-access-control/specs/*` 术语一致
- [x] 1.2 确认需求 006 验收标准与 spec scenarios 一一映射
- [x] 1.3 新增 ADR `docs/decisions/0008-platform-user-access-control.md`（D1–D8 摘要）

## 2. Flyway 与 org_node 种子（E1）

- [x] 2.1 编写 `org_node` 表 Flyway 迁移（code, name, parent_code, level）
- [x] 2.2 编写 SYSU_ORG 种子 SQL（从 mock 数据生成或脚本导入）
- [x] 2.3 验证种子：根节点 `SYSU` 存在且父子关系可遍历

## 3. 后端 — 领域层与 DataScopeResolver

- [x] 3.1 定义 DataScope 枚举、DepartmentRef、OperatorContext 值对象
- [x] 3.2 实现 DataScopeResolver（GLOBAL / OWN_DEPT / OWN_DEPT_AND_SUB + BFS 子孙）
- [x] 3.3 DataScopeResolver 单元测试（覆盖三档 + 边界 org code）

## 4. 后端 — 用户/角色/权限表与仓储

- [x] 4.1 Flyway：`platform_user`、`role`、`permission`、`user_role`、`role_permission`
- [x] 4.2 Flyway seed：admin 用户（BCrypt）、ADMIN 角色、全模块 read 权限、GLOBAL DataScope
- [x] 4.3 MyBatis-Plus Entity/Mapper：User、Role、Permission、OrgNode
- [x] 4.4 UserRepository：按 username 查询、DepartmentRef 校验 org_node 存在

## 5. 后端 — JWT 与 Spring Security

- [x] 5.1 添加 spring-boot-starter-security + jjwt 依赖
- [x] 5.2 实现 JwtTokenService（签发/解析 claims：sub, roles, permissions, dataScope, deptCode）
- [x] 5.3 实现 SelfBuiltAuthProvider + AuthAppService
- [x] 5.4 SecurityConfig：/api/auth/login public；其余 /api/** authenticated
- [x] 5.5 JwtAuthenticationFilter + OperatorContext 注入（SecurityContext / @CurrentOperator）

## 6. 后端 — API 与测试

- [x] 6.1 AuthController：POST login、GET me、POST logout
- [x] 6.2 UserAdminController：用户 CRUD + 角色分配（admin 权限保护）
- [x] 6.3 DemoController：GET `/api/demo/scoped-depts` 返回 scopedDeptCodes（DataScope 验收）
- [x] 6.4 AuthControllerTest：有效/无效登录、401/403
- [x] 6.5 DataScopeIntegrationTest：三档 DataScope + 绕过前端 API 过滤
- [x] 6.6 `mvn test` 全绿

## 7. 前端 — 真实认证对接

- [x] 7.1 auth API client（login、me、logout）
- [x] 7.2 重构 `stores/auth.ts`：Token + profile，移除 Mock 校验生产路径
- [x] 7.3 LoginView 对接 POST `/api/auth/login`
- [x] 7.4 router beforeEach：Token 校验 + public 路由例外
- [x] 7.5 按 permissions 过滤 AppTopbar / module-nav 可见项
- [x] 7.6 更新/新增 auth store 与 router 测试
- [x] 7.7 `npm run test` + `npm run build` 通过

## 8. 验收与 006 AC 勾选

- [x] 8.1 逐项勾选 `docs/requirements/inbox/006-platform-user-access-control.md` 8 条 AC
- [x] 8.2 确认 identity-access spec 每条 Requirement 至少一个 Scenario 有对应测试
- [x] 8.3 手动冒烟：admin 登录 → 模块可见 → scoped-depts API 返回正确范围（集成测试覆盖）

## 9. 归档与知识提升（apply 完成后执行）

- [x] 9.1 `/opsx:sync` — delta specs 合并到 `openspec/specs/`
- [x] 9.2 `/opsx:archive` — 归档 change `platform-user-access-control`
- [x] 9.3 `/opsx:sync-knowledge` — developing → established；更新 context-map 中 identity-access
- [x] 9.4 需求 `status: shipped` + `git mv` → `docs/requirements/shipped/006-platform-user-access-control.md`

## 10. 明确不在本 change 的任务

> 留给后续 change。

- SSO AuthProvider 实现
- ServiceAccount / 接入服务账号
- org-structure 完整（OrgMapping、OrgRoster、树编辑 UI）
- 各业务模块完整 DataScope 过滤（data-ingestion、identity-master 等）
