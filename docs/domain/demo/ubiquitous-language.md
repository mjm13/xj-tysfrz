# 统一语言（Ubiquitous Language，Demo 梳理）

> 术语优先对齐 `docs/domain/established/ubiquitous-language.md`；本文件仅记录 Demo 推导出的增量与页面追溯。

## 1. 核心名词

| 中文术语 | English | 上下文 | 定义 | Demo 线索 |
| --- | --- | --- | --- | --- |
| 自然人主档 | Person Record | identity-master | 平台对一个自然人的主数据表达，以 `PersonUID` 为唯一标识 | m1 基础数据列表、详情抽屉 |
| 人员 UID | PersonUID | identity-master | 平台级自然人唯一标识；≠ 学工号、证件号、平台登录 ID | m1「一人一 ID」 |
| 来源投影 | Source Projection | identity-master | 某采集源提供的人员字段快照，用于合并与冲突识别 | m1 多源头采集、变更时间轴 |
| 在档有效身份人员 | Active Identity Person | identity-master | 当前有效身份状态的自然人；默认不含校友/纯访客 | 主页在档人数 KPI |
| 冲突个案 | Conflict Case | identity-master | 多源属性冲突待人工裁定 | m1/m2 变更记录「冲突裁定」 |
| 变更记录 | Change Log | identity-master | 主档与治理操作的不可篡改审计链 | m1/m2/m4 变更视图 |
| 来源人员大类 | Source Person Category | data-ingestion | 接入时按业务划分的大类：教职工/学生/外协/校友/校外 | source-maintenance 五大类 |
| 采集源 | Data Source | data-ingestion | 向平台供数的业务系统或线下采集渠道 | source-maintenance、ETL |
| 字段映射 | Field Mapping | data-ingestion | 采集源字段 → 平台标准字段 | source-maintenance 字段映射表 |
| 采集任务 | Ingestion Job | data-ingestion | 从采集源同步数据的调度单元 | etl-monitor 任务列表 |
| 采集运行 | Ingestion Run | data-ingestion | 任务的一次执行实例 | etl-monitor 执行状态 |
| 分类节点 | Classification Node | identity-dimension | 校级统一人员分类树节点（141 节点/5 级） | m2 分类体系树 |
| 人员分类身份 | Person Classification | identity-dimension | `PersonUID` 挂载分类节点后的身份事实 | m2 人员分类数据 |
| 人员身份码 | Classification Code (rysfm) | identity-dimension | 分类节点业务编码，如 `010102` | m2 列表字段 rysfm |
| 映射规则 | Mapping Rule | identity-dimension | 来源原始类型 → 标准分类节点 | m2-admin 分类映射管理 |
| 未映射记录 | Unmapped Record | identity-dimension | 尚未归入标准分类的来源类型或人员 | m2 未映射人员告警 |
| 主身份 | Primary Identity | identity-dimension | 一人多类型时的主要身份标记 | m2 主身份展示 |
| 岗位身份 | Position Identity | identity-dimension | 人员在组织中的任职关系 | m3 岗位身份数据 |
| 岗位映射批次 | Position Mapping Batch | identity-dimension | 源头岗位原始数据标定入库的治理单元 | m3 岗位映射管理 |
| 组织节点 | Org Node | org-structure | 组织树节点，权威编码 `OrgNode.code` | m4 组织树（785 节点） |
| 组织映射 | Org Mapping | org-structure | 来源组织码 → 校级组织节点 | m4 组织映射 |
| 组织花名册 | Org Roster | org-structure | 组织下 PersonUID 列表（读模型） | m4 岗位花名册 |
| 标签群组 | Tag Group | identity-dimension | 自定义标签的分组容器 | m7 群组管理 |
| 自定义标签 | Custom Tag | identity-dimension | 业务柔性标注，不替代标准分类/岗位 | m7 标签标注 |
| 权限项 | Permission Item | permission-reconciliation | 可对账的公共权限类型 | m5 权限主数据、主页 4 权限 |
| 对账基线 | Reconciliation Baseline | permission-reconciliation | 分类 × 权限项的应然授权规则 | m5 授权矩阵 |
| 授权快照 | Grant Snapshot | permission-reconciliation | 外部权限系统的实然授权状态 | m5 数据源对接 |
| 对账差异 | Reconciliation Diff | permission-reconciliation | 基线 vs 快照不一致（越权/缺失等） | m5 对账异常 |
| 僵尸账号 | Stale Account | permission-reconciliation | 人员身份失效但权限仍在 | m5 僵尸账号视图 |
| 处置单 | Disposal Ticket | permission-reconciliation | 推送外部系统处理的工单 | m5 推送处置 |
| 查询策略 | Query Policy | data-query | 预制表/SQL 的表范围、行范围、审计规则 | m6 SQL 查询约束 |
| 预制查询视图 | Preset Query View | data-query | 身份 4 表 + 主题表的只读查询 | m6 预制数据表 |
| 平台操作者 | Interactive User | identity-access | 登录平台执行治理的人类账号 | 顶栏「管理员 · 数据治理岗」 |
| 数据范围 | Data Scope | identity-access | 操作者可见数据的部门口径 | （Demo 未显式， established 已定义） |

## 2. 核心动词（业务动作）

| 动词 | English | 上下文 | 含义 | Demo 页面/操作 |
| --- | --- | --- | --- | --- |
| 进档 | Enroll Person | identity-master | 自然人首次进入平台主档 | m1 同步入库 |
| 同步 | Sync / Ingest | data-ingestion | 从采集源拉取并转换数据 | ETL 立即同步、源头同步 |
| 映射 | Map | identity-dimension / org-structure | 来源值归入标准节点 | m2 映射规则、m4 组织映射 |
| 重新映射 | Remap | identity-dimension | 调整已有映射关系 | m2 批量重映射 |
| 采纳建议 | Accept Suggestion | identity-dimension | 采纳系统推荐的映射目标 | m2 未映射「采纳建议」 |
| 冲突裁定 | Resolve Conflict | identity-master | 人工处理多源冲突 | m1/m2 变更「冲突裁定」 |
| 标定入库 | Standardize & Persist | identity-dimension | 岗位映射明细确认后写入 | m3 标定入库 |
| 拆分 | Split (Cartesian) | identity-dimension | 多值职务×单位拆为一对一 | m3 笛卡尔积拆分 |
| 废弃节点 | Deprecate Node | identity-dimension / org-structure | 分类/组织节点下线并迁移 | m2-admin 废弃分类 |
| 对账 | Reconcile | permission-reconciliation | 基线与快照比对 | m5 检查任务执行 |
| 推送处置 | Push Disposal | permission-reconciliation | 将对账差异推送给外部系统 | m5 推送源头系统 |
| 标注 | Assign Tag | identity-dimension | 为人员打自定义标签 | m7 批量/单个标注 |
| 查询 | Query | data-query | 预制表筛选或 SQL 检索 | m6 查询/导出 |
| 启用/停用 | Enable / Disable | permission-reconciliation | 权限项或规则开关 | m5 权限项启停 |

## 3. 命名禁忌（与 established 一致）

| 禁用 | 统一用语 |
| --- | --- |
| 无限定词「源头」 | 采集源（DataSource）/ 权限系统（PermissionSystem） |
| 授权矩阵 | 对账基线矩阵 |
| 平台用户 = PersonUID | InteractiveUser ≠ PersonUID |
| 在前端 alone 做数据范围 | OperatorContext + 后端 ScopedDeptSet |

## 4. 待澄清术语边界

| 疑点 | 说明 |
| --- | --- |
| 校友 vs 在档 | 校友可接入，但是否计入主页「在档人数」需产品定义 |
| 有效授权状态 vs 分类状态 | m2 两字段并存，联动规则未在 Demo 写死 |
| 可申请授权 | m5 基线第三种状态；本期对账可检测，审批闭环未建模 |
