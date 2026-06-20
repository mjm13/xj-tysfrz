# Ubiquitous Language (Developing)

> **Change:** `identity-platform-domain`  
> **Status:** developing  
> **Last updated:** 2026-06-20

## 说明

本文件锁定高校综合身份数据平台的统一语言。**禁止**混用 demo 中「源头」一词指代不同概念。

## 平台级标识

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonUID（人员 UID） | identity-master | 平台级自然人唯一标识，跨分类/岗位/标签/权限/查询统一引用 | 格式 `UID` + 10 位数字，如 `UID0000123456`；**取代** demo 的 `NP*` / `UID-A*` |
| SourceRecordKey（源记录键） | data-ingestion | 某采集源内的人员或组织原始主键，与 PersonUID 多对一 | `{dataSourceId}:{sourceNativeId}` |
| 在档身份人员 | identity-master | 主档状态为有效、纳入平台统计的人员；**默认不含**校友/纯访客（与主页 94,283 口径对齐，见 QG-05） | `PersonRecord.status = ACTIVE` |

## 采集 vs 权限（QG-01）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| DataSource（采集源） | data-ingestion | 向平台**供给**人员/组织/分类数据的业务系统（人事、教务、研究生、协同平台等） | 原 source-maintenance / etl-monitor「源头维护」 |
| PermissionSystem（权限系统） | permission-reconciliation（外部） | 实际**执行授权**的业务系统（校园网、图书馆、进校权限、校园卡等） | m5「数据源对接」Tab；**不是** DataSource |
| IngestionJob（采集任务） | data-ingestion | 从某 DataSource 拉取并解析数据的调度单元 | ETL 监控页每一行任务 |
| IngestionRun（采集运行） | data-ingestion | 某任务的一次执行实例，产生成功/失败/增量统计 | etl-monitor 运行历史 |

## 人员主档（identity-master）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonRecord（人员主档） | identity-master | 自然人主数据聚合根；属性来自采集源投影 | 不可 UI 自由编辑字段 |
| SourceProjection（源投影） | identity-master | 同一 PersonUID 下来自不同 DataSource 的字段快照 | 多源冲突时进入 ConflictCase |
| ConflictCase（冲突个案） | identity-master | 多源对同一属性给出不同值的待裁定记录 | 取代 demo「编辑」按钮的误导向 |
| ChangeLog（变更记录） | identity-master | **唯一**审计链：采集写入 + 裁定结论 + 维度挂载变更 | 取代 GLOBAL_CHANGES / mockVersions 双轨 |
| 权威来源 | data-ingestion | 平台注册的 DataSource 个数；与主页「数据接入源」统计一致（当前 demo 7，不含权限系统） | 统一文案「采集源」 |

## 身份维度（identity-dimension）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| ClassificationNode（分类节点） | identity-dimension | 树形人员分类体系节点，code 来自标准树 | `SYSU_CLASSIFICATION` |
| PersonClassification（人员分类挂载） | identity-dimension | PersonUID 在某分类节点上的实例关系 | |
| MappingRule（映射规则） | identity-dimension | DataSource 字段值 → ClassificationNode 的映射 | admin 映射管理 |
| UnmappedRecord（未映射记录） | identity-dimension | 源头值无法匹配任何规则/节点的待治理项 | 单一 UnmappedQueue |
| PositionCatalog（岗位目录） | identity-dimension | 标准岗位枚举/树 | m3 主表 |
| PositionMapping（岗位映射） | identity-dimension | 源单位/岗位描述 → 标准岗位的治理数据 | **可写**；与岗位列表只读分离 |
| TagGroup（标签组） | identity-dimension | 自定义标签定义 | m7 |
| TagAssignment（标签标注） | identity-dimension | PersonUID 归属某 TagGroup | 必须引用 PersonUID，可选校验岗位 |

## 组织机构（org-structure）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| OrgNode（组织节点） | org-structure | 组织树节点，code 权威来自 `SYSU_ORG` | 禁止 demo 假 code `U00/ADM` |
| OrgMapping（组织映射） | org-structure | DataSource 组织编码 → OrgNode | |
| OrgRoster（组织花名册） | org-structure | 某 OrgNode 下在档 PersonUID 列表（投影） | 只读查询 |

## 权限对账（permission-reconciliation）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PermissionItem（权限项） | permission-reconciliation | 可对账的权限类型主数据（进校、校园网、图书馆…） | m5 catalog |
| ReconciliationBaseline（对账基线） | permission-reconciliation | 「某类人员应然拥有某权限项」的规则集合 | UI 称「**对账基线矩阵**」，**禁止**称「授权矩阵」 |
| GrantSnapshot（授权快照） | permission-reconciliation | 从 PermissionSystem 拉取的实然授权状态 | 只读 |
| ReconciliationDiff（对账差异） | permission-reconciliation | 基线 vs 快照的不一致 | reconcile / zombie 视图 |
| DisposalTicket（处置单） | permission-reconciliation | 推送 PermissionSystem 执行回收/补授权的工单 | 闭环核心；含状态 Pending/Sent/Acked |

## 数据查询（data-query）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| QueryPolicy（查询策略） | data-query | 表级 ACL、行级范围、脱敏、审计规则 | 绑定 identity-access 角色 |
| AdHocSQL（即席 SQL） | data-query | 受 QueryPolicy 约束的 SELECT；禁止绕过 PersonUID 范围 | m6 SQL 页 |

## 命名统一（QG-09）

| 禁用/混用 | 统一用语 |
| --- | --- |
| 标签身份 / 标签管理 / 自定义分类标签 | **自定义标签身份**（模块名）/ **标签组**（聚合） |
| 外协人员 / 外协类人员 | **外协类人员**（与 `SYSU_CLASSIFICATION` root 一致） |
| 源头（不分场景） | **采集源** 或 **权限系统**，必须带限定词 |
| 授权矩阵 | **对账基线矩阵** |

## 🔴 阻断项 → 术语处置

| 编号 | 处置 | 锁定术语/规则 |
| --- | --- | --- |
| QG-01 | 解决 | DataSource ≠ PermissionSystem |
| QG-04 | 解决 | PersonUID 全平台唯一 |
| QG-08 | 解决 | IngestionJob/Run 贯穿注册→采集→入库 |
| Q-M1-02 | 解决 | 主档只读；ConflictCase 裁定 |
| Q-M1-03 | 解决 | 单一 ChangeLog |
| Q-M2-02 | 解决 | UnmappedRecord + MappingRule |
| Q-M3-01 | 解决 | PositionMapping 治理写 vs 目录读 |
| Q-M3-02 | 解决 | 结构化 orgUnitCode，禁止 `/` 拆分 |
| Q-M4-01 | 解决 | OrgNode.code = SYSU_ORG |
| Q-M5-01 | 解决 | ReconciliationBaseline，禁「授权」动词 |
| Q-M5-02 | 解决 | DisposalTicket |
| Q-M6-01 | 解决 | QueryPolicy |
| Q-M2-01 | 解决 | （导航）子路由，非术语 |
| Q-SRC-01 | 推迟 | UI 重写，术语已覆盖 DataSource |
