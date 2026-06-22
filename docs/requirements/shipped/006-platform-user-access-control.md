---
title: 平台用户与访问控制（RBAC + 部门数据范围）
status: shipped
change: platform-user-access-control
openspecChange: openspec/changes/archive/2026-06-21-platform-user-access-control/
owner: team
createdAt: 2026-06-20
shippedAt: 2026-06-21
tier: 🔴
changeType: 业务
demoRef: docs/原始demo/（登录页）
relatedDomain: docs/domain/established/（identity-access 建模）
blocks: data-query, data-ingestion, identity-master, identity-dimension, org-structure, permission-reconciliation
supersedes: identity-access-mock（前端 Mock 登录将被真实鉴权替换）
---

# 背景

当前登录是前端 **Mock**（`identity-access-mock`：本地校验凭证 + 路由守卫 + 会话持久化），没有真实账号、角色、权限与数据范围控制。

需要区分两个**根本不同**的概念，避免混淆：

- **平台用户（操作者）**：登录平台、执行查询/治理/接入操作的人，需要 **角色、权限、部门归属、数据范围**。
- **被管理的自然人（数据）**：平台采集与治理的对象，由 `identity-master` 的 **PersonUID** 标识，**不是**登录账号。

本需求要落地**真实的访问控制上下文 `identity-access`**，替换 Mock，并提供横切所有业务上下文的 **RBAC + 依据部门的数据范围过滤**。这是 🔴 核心 change：权限模型与数据范围横切所有查询，定错方向会全局返工。

# 已锁定的关键决策

| 维度 | 决策 | 说明 |
| --- | --- | --- |
| 认证来源 | **自建为主 + 预留 SSO**（分阶段） | 先实现平台自建账号体系（用户名/密码 + 自管），架构上预留 CAS/OIDC/LDAP 对接扩展点，本期**不**实现 SSO 对接 |
| 数据范围模型 | **按部门层级自动派生** | 三档：`GLOBAL`（全局）/ `OWN_DEPT`（本部门）/ `OWN_DEPT_AND_SUB`（本部门及下级），基于 org-structure 组织树派生 |
| 部门体系来源 | **复用 org-structure 的 OrgNode** | 用户的「部门/归属」即 OrgNode 的 code，不另建行政部门树 |
| 主体范围（本期） | **仅交互式用户** | 领域模型预留 `Principal` 两子类（`InteractiveUser` / `ServiceAccount`），本期**只实现** `InteractiveUser`；接入服务账号留给后续独立 change |

# 业务目标

- 用真实的平台用户体系（账号 + 角色 + 权限 + 部门）替换前端 Mock 登录
- 提供基于角色的访问控制（RBAC），约束「谁能进入哪个模块 / 执行哪个操作」
- 提供基于部门的**数据范围过滤**，约束「谁能看到哪些部门口径的接入数据」
- 架构上预留 SSO 对接与接入服务账号扩展点，但不在本期落地

# 用例 / 用户故事

1. 作为系统管理员，我希望创建平台用户并分配角色与所属部门，以便控制其可用功能与数据范围。
2. 作为治理岗用户，我希望登录后只看到本部门及下级口径的接入数据，以便在职责范围内开展治理。
3. 作为全局管理员，我希望拥有 `GLOBAL` 数据范围，以便查看全校口径数据。
4. 作为平台用户，我希望未授权的模块/操作对我不可见或被拒绝，以便符合最小权限原则。
5. 作为架构，我希望认证来源可切换（自建 / 后续 SSO），以便未来对接学校统一身份认证时无需重写权限模型。

# 涉及限界上下文

- `identity-access`（**本期落地为真实上下文**，替换 `identity-access-mock`）：账号、角色、权限、数据范围、认证
- `org-structure`（**被引用**）：用户部门归属与数据范围派生的权威组织树（OrgNode code）
- `platform-shell`（**被影响**）：登录、路由守卫、按权限渲染导航
- `data-query` / `data-ingestion` 等（**消费方**）：在查询/接入读取链路应用数据范围过滤

# 关键领域概念（统一语言）

- `Principal（主体）`：可被认证与授权的实体；本期仅 `InteractiveUser` 子类，`ServiceAccount`（接入服务账号）仅预留概念。
- `InteractiveUser（交互式用户）`：人类操作者账号，含登录凭证、角色、部门归属、数据范围。
- `ServiceAccount（接入服务账号）`：**预留**——某采集源系统的机器身份，绑定 `dataSourceId`，仅接入授权，不进 RBAC 业务菜单（本期不实现）。
- `Role（角色）`：权限集合；用户通过角色获得 `Permission`。
- `Permission（权限项）`：对模块/操作的访问授权（功能权限）。
- `DepartmentRef（部门归属）`：用户所属 OrgNode 的 code（复用 org-structure，不另建）。
- `DataScope（数据范围）`：`GLOBAL` / `OWN_DEPT` / `OWN_DEPT_AND_SUB`，依据 `DepartmentRef` 与组织树派生用户可见的部门集合。
- `AuthSource（认证来源）`：`SELF_BUILT`（本期实现）/ `SSO`（预留扩展点）。

# 领域规则与不变量

- 平台用户 ≠ 自然人：`InteractiveUser` 不使用 PersonUID 作为身份；二者数据模型相互独立。
- 用户的 `DepartmentRef` MUST 是 org-structure 中存在的 OrgNode code（外键式引用，不复制组织树）。
- `DataScope = GLOBAL` 时可见全部部门；`OWN_DEPT` 仅本部门；`OWN_DEPT_AND_SUB` 为本部门及其在组织树下的所有子孙。
- 功能权限（RBAC）与数据范围（DataScope）是**两个正交维度**：能进入某模块 ≠ 能看到全部数据。
- 数据范围过滤 MUST 在后端查询链路统一施加，不依赖前端隐藏（前端隐藏仅作体验优化）。
- 认证来源可插拔：切换 `SELF_BUILT` ↔ `SSO` MUST NOT 改变 RBAC 与 DataScope 的语义。
- 最小权限：未显式授予的模块/操作默认拒绝。

# 非功能需求

- 安全：密码不得明文存储（加盐哈希）；登录态使用安全令牌；越权访问返回明确拒绝（403）。
- 可扩展性：认证来源与主体类型（`ServiceAccount`）通过扩展点接入，不破坏现有模型。
- 可观测性：登录、越权拒绝、数据范围生效应可审计。

# 验收标准

- [x] GIVEN 真实 `identity-access` 上线 WHEN 用户登录 THEN 使用自建账号体系（用户名/密码），不再走前端 Mock
- [x] GIVEN 管理员为用户分配角色 WHEN 用户登录 THEN 仅可访问角色授权的模块/操作，未授权操作被拒绝
- [x] GIVEN 用户 `DataScope = OWN_DEPT_AND_SUB` 且部门为某 OrgNode WHEN 查询接入数据 THEN 仅返回该部门及其子孙部门口径的数据
- [x] GIVEN 用户 `DataScope = GLOBAL` WHEN 查询接入数据 THEN 返回全校口径数据
- [x] GIVEN 用户部门归属 WHEN 设置 THEN 该部门 code MUST 存在于 org-structure 的 OrgNode
- [x] GIVEN 数据范围过滤 WHEN 前端被绕过直接调后端 THEN 后端仍按 DataScope 过滤（非前端兜底）
- [x] GIVEN 认证来源切换为 SSO 的扩展点 WHEN 查阅设计 THEN RBAC 与 DataScope 模型无需改动（仅预留，不实现）
- [x] GIVEN 主体模型 WHEN 查阅 THEN 含 `Principal` 抽象与 `InteractiveUser`/`ServiceAccount` 两子类，本期仅实现前者

# 备注

- **不在本期范围**：SSO 实际对接（CAS/OIDC/LDAP）、接入服务账号（ServiceAccount）的凭证签发/授权。
- 本 change 将使 `identity-access-mock` 退役；`platform-shell` 的登录与路由守卫需切换到真实鉴权。
- 与 `permission-reconciliation` 的区别：后者是「对账治理外部权限系统」，与本 change 的「平台自身访问控制」是两个上下文，勿混淆。
- 风险：数据范围过滤横切所有查询上下文，建议在 design 阶段统一过滤切面/查询规约，避免各上下文各写一套。
