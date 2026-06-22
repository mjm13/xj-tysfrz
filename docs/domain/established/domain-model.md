# 领域模型（Domain Model，已归档）

## 说明

本文件记录已归档、可稳定复用的领域模型。

## 模型清单

### identity-access

**聚合 InteractiveUserAccount：** InteractiveUser（platformUserId、username、passwordHash、status、departmentRef、dataScope）、DepartmentRef、Credential。

**聚合 RoleCatalog：** Role、Permission（`module:action`）、UserRole、RolePermission。

**领域服务：** AuthProvider、DataScopeResolver（GLOBAL / OWN_DEPT / OWN_DEPT_AND_SUB → ScopedDeptSet）。

**请求级：** OperatorContext（认证成功后注入，携带操作者、权限与可见部门范围）。

**不变量：** PlatformUserId ≠ PersonUID；未授权默认拒绝；ScopedDeptSet 后端统一计算；至少一名 GLOBAL 管理员（seed）。

**RoleCatalog 维护不变量（008）：**
- ADMIN 为系统角色：权限集合不可修改、角色不可删除（系统角色保护）。
- 角色 Permission 变更**不影响已在线用户的 session 权限**；变更仅对该角色用户**重新登录后**生效（session 权限在登录时快照，运行期不热更新）。
- admin 管理能力以 `admin:<资源>:read` / `admin:<资源>:write` 权限点表达（users / roles / departments）。

Spec：`docs/openspec/specs/identity-access/spec.md`；ADR：`docs/decisions/0008-platform-user-access-control.md`

### identity-master

**聚合 PersonRecord：** PersonUID（值对象）、SourceProjection（实体）、PersonUpserted 等领域事件。

**不变量：** PersonUID 不可变；禁止绕过主档治理自由修改；单一 ChangeLog。

**聚合 ConflictCase：** Open → Resolved | Dismissed；Resolved 写入 ChangeLog。

Spec：`docs/openspec/specs/identity-master/spec.md`

### identity-dimension

**classification：** ClassificationTree、MappingRuleSet（含 UnmappedRecord）、PersonClassification。

**position：** PositionCatalog（只读）、PositionMappingBatch（治理写）；禁止 `/` 拆单位名。

**custom-tag：** TagGroup、TagAssignment（必须 PersonUID）。

Spec：`docs/openspec/specs/identity-dimension/spec.md`

### org-structure

**聚合 OrgTree：** OrgNode（SYSU_ORG code）、OrgMapping；OrgRoster 读模型。

**已落地范围（006）：** Flyway `org_node` 种子表（790 节点），供 DepartmentRef 与 DataScopeResolver。

**平台管理维护不变量（009）：** OrgNode 可经平台管理 API（懒加载 roots/children + 新建/编辑）维护，受 `admin:departments:*` 保护。
- code 全局唯一；
- parent_code 为空（根）或引用已存在 OrgNode；
- 更新 parent 不得成环（不可设为自身或子孙）；
- level 由 `parent.level + 1` 派生（根 level=1）；
- 本切片**不提供删除**：有 platform_user 挂靠或有子节点的节点删除留待后续 change（Deferred）。

Spec：`docs/openspec/specs/org-structure/spec.md`

### data-ingestion

**聚合 DataSource、IngestionJob/IngestionRun；** 注册必须被 Job 消费；Run 完成发布领域事件。

Spec：`docs/openspec/specs/data-ingestion/spec.md`

### permission-reconciliation

**ReconciliationBaseline（只读对账输入）、ReconciliationRun、DisposalTicket**（Draft → Pushed → Acknowledged | Failed）。

**不变量：** 平台不执行授权。

Spec：`docs/openspec/specs/permission-reconciliation/spec.md`

### data-query

**QueryPolicy** 约束 AdHocSQL（SELECT only、表白名单、审计）。

Spec：`docs/openspec/specs/data-query/spec.md`

---

## 跨上下文规则

- 人员关联仅通过 PersonUID
- OrgNode.code 以 org-structure 为权威
- 在档统计 = identity-master ACTIVE，默认不含校友/纯访客

完整聚合表见归档 `identity-platform-domain/design.md` 与 `docs/decisions/0007-identity-platform-domain-decisions.md`。
