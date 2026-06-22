# Domain Model（Demo 梳理）

> 战术设计聚焦核心上下文。聚合边界依据**业务不变量**，非数据库表结构。

## 1. identity-master

### 聚合根：PersonRecord

| 类型 | 名称 | 说明 |
| --- | --- | --- |
| 聚合根 | PersonRecord | 自然人主数据入口 |
| 实体 | SourceProjection | 某 DataSource 的字段快照 |
| 实体 | ChangeLogEntry | 变更审计条目 |
| 值对象 | PersonUID | 不可变唯一标识 |
| 值对象 | SourceRecordKey | `{dataSourceId}:{sourceNativeId}` |
| 值对象 | IdentityDocument, PersonName, ContactInfo | 主档属性 |

**领域行为**：`upsertFromSource()` · `linkSourceProjection()` · `markInactive()` · `recordChange()`

**领域事件**：`PersonRecordCreated` · `PersonRecordUpdated` · `PersonStatusChanged` · `SourceProjectionLinked`

**为什么这么拆**：强一致性边界是「一个自然人 ↔ 一个稳定 PersonUID」。分类、岗位可变，但 UID 与审计链不可被下游绕过。

### 聚合根：ConflictCase

| 类型 | 名称 |
| --- | --- |
| 实体 | ConflictEvidence, ResolutionDecision |
| 值对象 | ConflictType, ResolutionReason |

**领域行为**：`openConflict()` · `resolve()` · `dismiss()`

**领域事件**：`ConflictCaseOpened` · `ConflictCaseResolved`

---

## 2. identity-dimension

### 聚合根：ClassificationTree

| 类型 | 名称 |
| --- | --- |
| 实体 | ClassificationNode |
| 值对象 | ClassificationCode, ClassificationLevel, EffectivePeriod |

**领域行为**：`addNode()` · `renameNode()` · `deprecateNode()` · `moveNode()`

**领域事件**：`ClassificationNodeAdded` · `ClassificationNodeDeprecated` · `ClassificationTreeChanged`

### 聚合根：MappingRuleSet

| 类型 | 名称 |
| --- | --- |
| 实体 | MappingRule, UnmappedRecord |
| 值对象 | SourceTypeKey, MappingSuggestion |

**领域行为**：`mapSourceType()` · `remapSourceType()` · `acceptSuggestion()` · `markUnmapped()`

**领域事件**：`MappingRuleCreated` · `SourceTypeRemapped` · `UnmappedRecordDetected` · `UnmappedRecordResolved`

**不变量**：同一来源系统 + 原始类型 → 唯一标准分类节点。

### 聚合根：PersonClassification

| 类型 | 名称 |
| --- | --- |
| 实体 | ClassificationAssignment |
| 值对象 | PersonUID, ClassificationCode, ClassificationStatus, PrimaryIdentityFlag |

**领域行为**：`attachClassification()` · `changeStatus()` · `setPrimaryIdentity()` · `expireClassification()`

**领域事件**：`PersonClassificationAttached` · `PrimaryIdentityChanged` · `PersonClassificationStatusChanged`

### 聚合根：PositionIdentity（含 PositionMappingBatch）

| 类型 | 名称 |
| --- | --- |
| 实体 | PositionAssignment, PositionMappingBatch |
| 值对象 | PositionName, AppointmentStatus, OrgRef, SourcePositionKey |

**领域行为**：`loadSourcePositions()` · `splitMultiValuePosition()` · `standardizePosition()` · `attachPosition()`

**领域事件**：`PositionSourceLoaded` · `PositionMappingStandardized` · `PersonPositionAttached`

### 聚合根：TagGroup

| 类型 | 名称 |
| --- | --- |
| 实体 | CustomTag, TagAssignment |
| 值对象 | TagName, TagScope, PersonUID |

**领域行为**：`createTag()` · `deleteTag()` · `assignTag()` · `removeTag()`

**为什么这么拆**：分类树维护、映射规则、人员挂载、岗位标定变化频率与不变量不同，不宜合并为一个大聚合。

---

## 3. org-structure

### 聚合根：OrgTree

| 类型 | 名称 |
| --- | --- |
| 实体 | OrgNode, OrgMapping |
| 值对象 | OrgCode, OrgType, OrgLevel, OrgStatus |

**领域行为**：`addOrgNode()` · `renameOrgNode()` · `mergeOrgNode()` · `mapSourceOrg()`

**领域事件**：`OrgNodeAdded` · `OrgNodeChanged` · `OrgNodeMerged` · `OrgMappingChanged`

**不变量**：`OrgNode.code` 以 org-structure 为权威；岗位/分类仅引用 code。

---

## 4. data-ingestion

### 聚合根：DataSource

| 类型 | 名称 |
| --- | --- |
| 实体 | SourceEndpoint, FieldMapping, SourceCategoryBinding |
| 值对象 | SourceCode, ProtocolType, SyncStrategy, UniqueKeyDefinition |

**领域行为**：`registerSource()` · `bindCategory()` · `changeFieldMapping()` · `requestSourceChangeApproval()`

**领域事件**：`DataSourceRegistered` · `SourceCategoryBound` · `FieldMappingChanged` · `SourceChangeApprovalRequested`

**不变量**：每个来源人员大类仅绑定一个权威采集源；换源需审批。

### 聚合根：IngestionJob

| 类型 | 名称 |
| --- | --- |
| 实体 | IngestionRun, RunStep |
| 值对象 | JobSchedule, RunStatus, RunSummary |

**领域行为**：`runOnce()` · `runSequentially()` · `completeRun()` · `failRun()`

**领域事件**：`IngestionRunStarted` · `IngestionRunCompleted` · `IngestionRunFailed` · `SourceClassificationSynced`

**不变量**：分类相关任务有先后依赖（类型表同步 → 人员情况比对入库）。

---

## 5. permission-reconciliation

### 聚合根：PermissionCatalog · ReconciliationBaseline · ReconciliationRun · DisposalTicket

| 聚合根 | 核心实体 | 关键值对象 |
| --- | --- | --- |
| PermissionCatalog | PermissionItem | PermissionCode, PermissionStatus |
| ReconciliationBaseline | BaselineRule | GrantPolicy, ClassificationRef, PermissionRef |
| ReconciliationRun | GrantSnapshot, ReconciliationDiff | DiffType, DiffSeverity |
| DisposalTicket | DisposalAction, PushAttempt | TicketStatus, ExternalSystemRef |

**GrantPolicy 枚举**：`DEFAULT_GRANT`（默认授予）· `APPLY_REQUIRED`（可申请）· `DENIED`（不予授权）

**领域事件**：`ReconciliationRunCompleted` · `ReconciliationDiffDetected` · `DisposalTicketCreated` · `DisposalTicketPushed` · `DisposalTicketAcknowledged`

**为什么这么拆**：平台判断应然与差异；授权发放由外部 PermissionSystem 执行。一次对账运行内的快照、基线、差异为强一致边界。

---

## 6. data-query

### 聚合根：QueryPolicy

| 类型 | 名称 |
| --- | --- |
| 实体 | QueryViewDefinition, SqlWhitelistRule, QueryAuditRecord |
| 值对象 | TableRef, FieldRef, DataScopeRef |

**领域行为**：`allowPresetQuery()` · `validateAdHocSql()` · `recordQueryAudit()`

**领域事件**：`QueryExecuted` · `AdHocSqlRejected`

---

## 7. 跨上下文不变量

- 人员关联仅通过 `PersonUID`；跨聚合只存 ID，不持有对象。
- `PlatformUserId`（InteractiveUser）≠ `PersonUID`（自然人）。
- 平台不直接修改采集源人员明细；不执行授权发放。
- 已绑定映射或存在人员挂载的分类节点不可直接删除，只能废弃并迁移。
- 岗位身份以源头部门/线下采集为主，平台侧治理标定 + 只读展示。
- 在档统计默认 = identity-master ACTIVE，不含校友/纯访客（待产品确认校友口径）。

## 8. 待产品确认（影响模型细化）

1. 校友活跃状态是否参与在档 KPI 与对账？
2. 学生大类双源（教务 + 研究生）的权威来源策略？
3. 分类状态 ↔ 有效授权状态是否自动联动？
4. 未映射处理完成后是否立即触发对账重算？
5. 组织树权威来源：平台维护 vs 外部下发？
6. 「可申请授权」审批闭环由平台还是外部 PermissionSystem 承担？
