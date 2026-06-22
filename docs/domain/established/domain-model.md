# Domain Model (Established)

## 说明

本文件记录已归档、已落地的领域模型。

## 模型清单

### identity-access

**聚合 InteractiveUserAccount：** InteractiveUser（platformUserId、username、passwordHash、status、departmentRef、dataScope）、DepartmentRef、Credential。

**聚合 RoleCatalog：** Role、Permission（`module:action`）、UserRole、RolePermission。

**领域服务：** AuthProvider（SelfBuiltAuthProvider 本期）、DataScopeResolver（GLOBAL / OWN_DEPT / OWN_DEPT_AND_SUB → ScopedDeptSet）。

**请求级：** OperatorContext（JWT 解析后注入）。

**不变量：** PlatformUserId ≠ PersonUID；未授权默认拒绝；ScopedDeptSet 后端统一计算；至少一名 GLOBAL 管理员（seed）。

**预留：** Principal 抽象 + ServiceAccount 子类（本期不实现）。

Spec：`openspec/specs/identity-access/spec.md`；ADR：`docs/decisions/0008-platform-user-access-control.md`

### platform-shell

> 纯 UI 壳层，无业务聚合。登录对接 identity-access JWT；见 `ubiquitous-language` 与 `openspec/specs/platform-shell`。

---

### identity-master

**聚合 PersonRecord：** PersonUID（值对象）、SourceProjection（实体）、PersonUpserted 等领域事件。

**不变量：** PersonUID 不可变；禁止 UI/API 自由 PATCH；单一 ChangeLog。

**聚合 ConflictCase：** Open → Resolved | Dismissed；Resolved 写入 ChangeLog。

Spec：`openspec/specs/identity-master/spec.md`

### identity-dimension

**classification：** ClassificationTree、MappingRuleSet（含 UnmappedRecord）、PersonClassification。

**position：** PositionCatalog（只读）、PositionMappingBatch（治理写）；禁止 `/` 拆单位名。

**custom-tag：** TagGroup、TagAssignment（必须 PersonUID）。

Spec：`openspec/specs/identity-dimension/spec.md`

### org-structure

**聚合 OrgTree：** OrgNode（SYSU_ORG code）、OrgMapping；OrgRoster 读模型。

**最小落地（006）：** Flyway `org_node` 种子表（790 节点），供 DepartmentRef 与 DataScopeResolver；完整 OrgMapping/Roster UI 留给后续 change。

Spec：`openspec/specs/org-structure/spec.md`

### data-ingestion

**聚合 DataSource、IngestionJob/IngestionRun；** 注册必须被 Job 消费；Run 完成发布领域事件。

Spec：`openspec/specs/data-ingestion/spec.md`

### permission-reconciliation

**ReconciliationBaseline（只读对账输入）、ReconciliationRun、DisposalTicket**（Draft → Pushed → Acknowledged | Failed）。

**不变量：** 平台不执行授权。

Spec：`openspec/specs/permission-reconciliation/spec.md`

### data-query

**QueryPolicy** 约束 AdHocSQL（SELECT only、表白名单、审计）。

Spec：`openspec/specs/data-query/spec.md`

---

## 跨上下文规则

- 人员关联仅通过 PersonUID
- OrgNode.code 以 org-structure 为权威
- 在档统计 = identity-master ACTIVE，默认不含校友/纯访客

## 后续实现 change 映射

| 上下文 | OpenSpec change |
| --- | --- |
| identity-access | `platform-user-access-control`（已归档） |
| identity-master | `basic-identity` |
| identity-dimension | `classification-identity` 等 |
| org-structure | `org-structure` |
| data-ingestion | `data-ingestion` |
| permission-reconciliation | `identity-permission` |
| data-query | `data-query-service` |

完整聚合表见归档 `identity-platform-domain/design.md` 与 `docs/decisions/0007-identity-platform-domain-decisions.md`。
