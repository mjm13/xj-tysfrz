# Ubiquitous Language (Established)

## 说明

本文件记录已归档、已稳定复用的统一语言术语。

## 壳层（platform-shell / identity-access-mock）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| 应用壳层（App Shell） | platform-shell | 登录页 + 双布局 + 全局路由，不含具体业务 | `App.vue` / `router/` |
| 主布局（Main Layout） | platform-shell | 首页布局：顶栏 + 无侧栏主内容区 | `layouts/MainLayout.vue` |
| 模块布局（Module Layout） | platform-shell | 业务页布局：顶栏 + 模块侧栏 + 主内容区 | `layouts/ModuleLayout.vue` |
| 设计令牌（Design Token） | platform-shell | 颜色、字体、圆角、阴影等全局 CSS 变量 | `assets/styles/tokens.css` |
| Mock 登录 | identity-access-mock | 不调后端、前端本地校验凭证并建立会话 | `stores/auth.ts` |
| 路由守卫（AuthGuard） | identity-access-mock | 依据会话状态控制路由访问 | `router/index.ts` |
| 会话存储（SessionStore） | identity-access-mock | Pinia 管理的认证态 | `stores/auth.ts` |

## 平台级标识

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonUID（人员 UID） | identity-master | 平台级自然人唯一标识 | `UID` + 10 位数字，如 `UID0000123456` |
| SourceRecordKey（源记录键） | data-ingestion | 采集源内原始主键 | `{dataSourceId}:{sourceNativeId}` |
| 在档身份人员 | identity-master | 有效主档人员，默认不含校友/纯访客 | `PersonRecord.status = ACTIVE` |

## 采集 vs 权限

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| DataSource（采集源） | data-ingestion | 向平台供数的业务系统 | 人事、教务等 |
| PermissionSystem（权限系统） | permission-reconciliation（外部） | 执行授权的业务系统 | 校园网、图书馆等；**不是** DataSource |
| IngestionJob（采集任务） | data-ingestion | 从 DataSource 拉数的调度单元 | |
| IngestionRun（采集运行） | data-ingestion | 任务的一次执行实例 | |

## 人员主档（identity-master）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonRecord（人员主档） | identity-master | 自然人主数据聚合根 | 不可 UI 自由编辑 |
| SourceProjection（源投影） | identity-master | 某 DataSource 的字段快照 | |
| ConflictCase（冲突个案） | identity-master | 多源属性冲突待裁定 | |
| ChangeLog（变更记录） | identity-master | 唯一审计链 | |

## 身份维度（identity-dimension）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| ClassificationNode（分类节点） | identity-dimension | 人员分类树节点 | `SYSU_CLASSIFICATION` |
| PersonClassification | identity-dimension | PersonUID 挂载分类 | |
| MappingRule（映射规则） | identity-dimension | 源值 → 分类节点 | |
| UnmappedRecord（未映射记录） | identity-dimension | 待治理未映射项 | UnmappedQueue |
| PositionCatalog（岗位目录） | identity-dimension | 标准岗位（只读投影） | |
| PositionMapping（岗位映射） | identity-dimension | 源描述 → 标准岗位（治理写） | |
| TagGroup / TagAssignment | identity-dimension | 自定义标签组与标注 | |

## 组织机构（org-structure）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| OrgNode（组织节点） | org-structure | 组织树节点 | code 来自 `SYSU_ORG` |
| OrgMapping（组织映射） | org-structure | 源组织码 → OrgNode | |
| OrgRoster（组织花名册） | org-structure | 组织下 PersonUID 列表（投影） | |

## 权限对账（permission-reconciliation）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PermissionItem（权限项） | permission-reconciliation | 可对账权限类型主数据 | |
| ReconciliationBaseline（对账基线） | permission-reconciliation | 应然授权规则 | UI：**对账基线矩阵** |
| GrantSnapshot（授权快照） | permission-reconciliation | 从 PermissionSystem 拉取的实然状态 | 只读 |
| ReconciliationDiff（对账差异） | permission-reconciliation | 基线 vs 快照不一致 | |
| DisposalTicket（处置单） | permission-reconciliation | 推送 PermissionSystem 的工单 | |

## 数据查询（data-query）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| QueryPolicy（查询策略） | data-query | 表 ACL、行级范围、审计 | |
| AdHocSQL（即席 SQL） | data-query | 受策略约束的 SELECT | |

## 命名禁忌

| 禁用 | 统一用语 |
| --- | --- |
| 无限定词「源头」 | 采集源 / 权限系统 |
| 授权矩阵 | 对账基线矩阵 |

详细术语与阻断项映射见 ADR [`0007-identity-platform-domain-decisions.md`](../../decisions/0007-identity-platform-domain-decisions.md)。
