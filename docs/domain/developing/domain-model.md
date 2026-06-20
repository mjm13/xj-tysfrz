# Domain Model (Developing)

> **Change:** `identity-platform-domain`  
> **Status:** developing  
> **Last updated:** 2026-06-20

## 说明

各限界上下文的聚合、实体、值对象与核心不变量。实现 change（basic-identity 等）须遵循本文，不得复刻 demo 矛盾模型。

---

## identity-master（人员基础身份）

### 聚合：PersonRecord

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | PersonRecord | 自然人主档 |
| 实体 | SourceProjection | 某 DataSource 下的字段快照 |
| 值对象 | PersonUID | 平台唯一标识 |
| 值对象 | PersonName, IdDocument, ContactInfo | 主档属性（多源合并） |
| 领域事件 | PersonUpserted, FieldConflictDetected, ConflictResolved | |

**不变量**

1. PersonUID 全局唯一，创建后不可变更。
2. **禁止** UI 或 API 直接 PATCH 主档属性；变更仅来自 `data-ingestion` 写入或 `ConflictCase` 裁定。
3. 每个 SourceProjection 必须携带 `dataSourceId` + `sourceRecordKey`。
4. ChangeLog 为审计唯一出口；禁止并行 mock 时间轴。

### 聚合：ConflictCase（子流程，可同模块）

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | ConflictCase | 多源冲突待办 |
| 值对象 | ConflictField, SourceValue | |

**不变量**

- 状态机：`Open → Resolved | Dismissed`；Resolved 必须写入 ChangeLog 并选定 authoritativeSource。

---

## identity-dimension

### 子域：classification

#### 聚合：ClassificationTree

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | ClassificationTree | 整树版本化治理 |
| 实体 | ClassificationNode | code 唯一 |
| 领域事件 | ClassificationNodeAdded, ClassificationNodeMoved | |

**不变量**

- 节点 code 与 `SYSU_CLASSIFICATION` 编码策略一致；最大深度 ≥ 5（覆盖 demo 5 级）。
- 有 PersonClassification 挂载的节点不可物理删除，只能停用。

#### 聚合：MappingRuleSet

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | MappingRuleSet | 某 DataSource 的分类映射规则集 |
| 实体 | MappingRule | 源字段表达式 → targetNodeCode |
| 实体 | UnmappedRecord | 待治理队列项 |

**不变量**

- UnmappedRecord 闭环：`Detected → Mapped | Ignored`；必须能追溯到 source 值与 ingestionRunId。
- 主站与 admin **共享**同一 UnmappedQueue 读模型（解决 Q-M2-04 双存储）。

#### 聚合：PersonClassification

- 关联 `(PersonUID, classificationNodeCode)` 唯一。

### 子域：position

#### 聚合：PositionCatalog（读模型为主）

- 标准岗位条目；来自采集 + 治理确认。

#### 聚合：PositionMappingBatch

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | PositionMappingBatch | 一次映射治理提交 |
| 实体 | PositionMappingLine | 源描述 → 标准岗位 |

**不变量**

1. 列表页 PositionCatalog **只读**；写操作仅通过 PositionMappingBatch。
2. 源单位必须使用 `orgUnitCode` 结构化字段；**禁止**字符串 `/` 拆分（Q-M3-02）。
3. `invalid/pending` 行不可入库；savedCount 仅计 `Confirmed` 行。
4. 保存后发布 PositionCatalogUpdated 事件刷新读模型（Q-M3-05）。

### 子域：custom-tag

#### 聚合：TagGroup

#### 聚合：TagAssignment

**不变量**

- TagAssignment 必须引用有效 PersonUID。
- 「基于岗位身份」为可选约束：添加人员时若启用则校验 PersonPosition 存在。

---

## org-structure（组织机构）

### 聚合：OrgTree

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | OrgTree | 组织树版本 |
| 实体 | OrgNode | code 来自 SYSU_ORG |
| 实体 | OrgMapping | 源组织码 → orgNodeCode |

**不变量**

1. OrgNode.code **必须**存在于权威组织码表（`SYSU_ORG`）；demo 假 code 禁止入库（Q-M4-01）。
2. 树编辑产生 ChangeLog；禁止仅 alert 不落库（Q-M4-02）。
3. 层级筛选支持 L1–L5。

### 读模型：OrgRoster

- `(orgNodeCode, PersonUID)` 投影，由 ingestion + 维度关系生成。

---

## data-ingestion（数据接入）

### 聚合：DataSourceRegistry

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | DataSource | 采集源注册 |
| 值对象 | DataSourceType, ConnectionConfig, FieldMappingTemplate | |
| 实体 | CategoryBinding | 人员大类 ↔ 唯一主 DataSource（Q-SRC-02：一大类一主源，多子源用 Supplemental 角色） |

**不变量**

- DataSource 仅描述**采集**，不描述 PermissionSystem。
- 注册变更必须被 IngestionJob 引用；禁止「注册了但 ETL 不消费」（QG-08）。

### 聚合：IngestionJob

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | IngestionJob | 调度定义 |
| 实体 | IngestionRun | 单次运行 |
| 领域事件 | IngestionRunCompleted, RecordsUpserted | |

**不变量**

- Run 完成后必须发布领域事件供 identity-master / identity-dimension / org-structure 消费。
- m1 内嵌 sync/source 视图删除，统一跳转 data-ingestion UI（Q-M1-01）。

---

## permission-reconciliation（权限对账治理）

### 聚合：PermissionCatalog

- PermissionItem 主数据。

### 聚合：ReconciliationBaseline

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | ReconciliationBaseline | 人员类型 × 权限项 应然规则 |
| 值对象 | BaselineRule | |

**不变量**

- **平台不执行授权**；Baseline 仅只读展示与对账输入（Q-M5-01）。
- UI 禁止出现「授权」「开通」类写操作按钮。

### 聚合：ReconciliationRun

- 输入：Baseline + GrantSnapshot（从 PermissionSystem 拉取）
- 输出：ReconciliationDiff（含 zombie 子类）

### 聚合：DisposalTicket

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | DisposalTicket | 推送 PermissionSystem 的处置工单 |
| 值对象 | DisposalAction | RECOVER / GRANT_REQUEST（仅请求，平台不执行） |

**不变量**

- 状态机：`Draft → Pushed → Acknowledged | Failed`（Q-M5-02 闭环）。
- MVP 允许 Ack 为 stub，但模型与 API 必须存在。

---

## data-query（数据查询）

### 聚合：QuerySession（应用服务级，可选）

**不变量（Q-M6-01）**

1. 每次查询绑定 `operatorId` + QueryPolicy。
2. AdHocSQL 仅允许 SELECT；表白名单 + 行级 PersonUID 范围。
3. 全量审计：who / when / sqlHash / rowCount。
4. 「人员权限对应信息」宽表为 **permission-reconciliation 的 CQRS 读模型**，非权威授权源（Q-M6-03）。

---

## identity-access（访问控制）

> 沿用 established mock；真实 RBAC 在独立 change 扩展。

- 与 QueryPolicy、各 API 资源 scope 对齐。

---

## 跨上下文一致性

| 规则 | 说明 |
| --- | --- |
| PersonUID 引用 | 所有上下文仅通过 PersonUID 关联人员 |
| 组织 code | org-structure 为 OrgNode 唯一权威；维度/岗位引用 orgUnitCode |
| 变更审计 | 各上下文本地 ChangeLog 可关联同一 `correlationId`（ingestionRunId 或 ticketId） |
| 统计口径 | 「在档人数」= identity-master ACTIVE 且不含校友/纯访客；主页与 m1 统一 |

---

## 后续实现 change 映射

| 领域上下文 | 建议 OpenSpec change |
| --- | --- |
| identity-master | `basic-identity` |
| identity-dimension.classification | `classification-identity` |
| identity-dimension.position | `position-identity`（或合入 classification change） |
| identity-dimension.custom-tag | `custom-tag-identity` |
| org-structure | `org-structure` |
| data-ingestion | `data-ingestion` |
| permission-reconciliation | `identity-permission` |
| data-query | `data-query-service` |
| identity-access | `identity-access`（替换 mock） |
