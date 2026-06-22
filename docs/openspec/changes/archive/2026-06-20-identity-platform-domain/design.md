# Design: identity-platform-domain

> **业务 change**：本文档以**领域设计**为主；本 change **不编写应用代码**。

## Context

- 前端壳层（004）已交付 ModuleLayout + 占位路由；后端仅有平台基座（Ping、Flyway baseline），无业务表。
- Demo（m1~m7 + ETL + 源头维护）为产品意图参考，但含大量 mock 矛盾，不可逐字复刻。
- Explore 阶段已在 `docs/domain/developing/` 写入 context-map、ubiquitous-language、domain-model 初稿。

## Goals / Non-Goals

**Goals:**

- 为 6 个业务限界上下文提供可测试的不变量（OpenSpec specs）
- 关闭 QG-01/04/08、Q-M1-02/03、Q-M2-02、Q-M3-01/02、Q-M4-01、Q-M5-01/02、Q-M6-01 等领域层决策
- 明确后续实现 change 的依赖顺序与集成模式

**Non-Goals:**

- Flyway 表结构、Controller/Service 实现
- 权限系统外部对接的具体协议（SOAP/REST/消息队列）——留 `identity-permission` change
- SQL 引擎选型——留 `data-query-service` change

## 领域设计

### 限界上下文地图

详见 [`docs/domain/developing/context-map.md`](../../../docs/domain/developing/context-map.md)。

核心划分：

| Context | 聚合根（摘要） |
| --- | --- |
| identity-master | PersonRecord, ConflictCase |
| identity-dimension | ClassificationTree, MappingRuleSet, PositionMappingBatch, TagGroup |
| org-structure | OrgTree |
| data-ingestion | DataSource, IngestionJob |
| permission-reconciliation | ReconciliationBaseline, DisposalTicket |
| data-query | QueryPolicy（策略对象，非业务聚合） |

### 统一语言要点

详见 [`docs/domain/developing/ubiquitous-language.md`](../../../docs/domain/developing/ubiquitous-language.md)。

| 决策 | 内容 |
| --- | --- |
| PersonUID | `UID` + 10 位数字，全平台唯一人员引用 |
| 源头拆分 | DataSource（采集）≠ PermissionSystem（授权执行方） |
| 权限 UI | 「对账基线矩阵」，禁止「授权矩阵」及平台侧授权写操作 |
| 在档口径 | ACTIVE 主档，默认不含校友/纯访客（对齐主页统计） |

### 跨上下文规则

1. 人员关联**仅**通过 PersonUID。
2. 组织编码**仅**以 org-structure 的 OrgNode.code（SYSU_ORG）为权威。
3. 主档变更**仅**来自 data-ingestion 或 ConflictCase 裁定。
4. permission-reconciliation 对 PermissionSystem **只读快照 + 出站 DisposalTicket**。
5. data-query 读模型不得成为授权或对账的权威源。

### 🔴 阻断项处置摘要

| 编号 | 处置 |
| --- | --- |
| QG-01, QG-04, QG-08 | 解决 — 见统一语言与 data-ingestion 闭环 |
| Q-M1-02, Q-M1-03 | 解决 — 主档只读 + 单一 ChangeLog |
| Q-M2-01 | 解决 — 004 子路由；m2 主站/admin 同上下文 |
| Q-M2-02 | 解决 — UnmappedRecord 显式队列 |
| Q-M3-01, Q-M3-02 | 解决 — 映射治理写 / 列表读；结构化 orgUnitCode |
| Q-M4-01 | 解决 — SYSU_ORG 权威 code |
| Q-M5-01, Q-M5-02 | 解决 — 对账定位 + DisposalTicket |
| Q-M6-01 | 解决 — QueryPolicy（实现推迟到 data-query-service） |
| Q-SRC-01 | 推迟 — UI 重写，不阻塞模型 |

## Decisions

### D1: PersonUID 格式

- **选择：** `UID` + 10 位零填充数字（如 `UID0000123456`）
- **理由：** 统一 demo 的 `NP*` 与 `UID-A*`；人类可读、固定宽度便于索引
- **备选：** UUID（对运营不友好）；纯数字无前缀（与源系统 ID 易混淆）

### D2: 主档写策略

- **选择：** UI/API 禁止直接 PATCH 主档；ConflictCase 裁定 + ingestion 事件
- **理由：** 闭合 Q-M1-02「不可编辑 vs 编辑按钮」矛盾
- **备选：** 允许管理员自由编辑（破坏多源真相）

### D3: 权限模块边界

- **选择：** permission-reconciliation 只做对账与处置推送，不持有授权写 API
- **理由：** 闭合 Q-M5-01；与高校「权限在业务系统执行」一致
- **备选：** 平台内直接授权（与 demo 文案冲突且 scope 膨胀）

### D4: 数据接入单上下文

- **选择：** source-maintenance + etl-monitor + m1 内嵌 sync 收敛为 data-ingestion
- **理由：** 闭合 QG-08 三套并存
- **备选：** 保持 demo 三套 UI 各自独立配置（配置无法消费）

### D5: platform-shell 文档纠偏

- **选择：** OpenSpec delta 更新为顶栏 + ModuleLayout（004 既成事实）
- **理由：** P0-09 文档漂移；established 术语「232px 侧栏指标平台」已过时

## 分层裁剪决策（供后续实现 change 引用）

| 后续 change | 建议分层 | 理由 |
| --- | --- | --- |
| identity-master, permission-reconciliation | **完整 DDD 分层** | 多源冲突、状态机、领域事件 |
| identity-dimension.classification | **完整 DDD** | 树版本化、映射规则、未映射队列 |
| org-structure, data-ingestion | **简化三层 + 领域服务** | CRUD 为主，不变量中等 |
| data-query | **简化三层** | 策略 enforcement 在应用层，无复杂聚合 |
| 本 change（建模） | **无代码分层** | 仅文档与 OpenSpec |

## Risks / Trade-offs

| 风险 | 缓解 |
| --- | --- |
| 模型与 demo UI 短期不一致 | 各 ui-* change 按 spec 改交互，不反向改模型迁就 demo |
| DisposalTicket 外部回执未定义 | MVP stub Ack；spec 要求状态机存在 |
| PersonUID 与 legacy 源 ID 映射 | SourceRecordKey 独立存储，ingestion 维护映射表 |
| 6 个 spec 并行维护成本 | context-map 为索引；术语表单一真相源 |

## Migration Plan

1. **本 change apply**：核对 developing 三件套与 delta specs 一致；无数据库迁移。
2. **archive + sync-knowledge**：developing → established；sync 6 个新 spec + platform-shell delta 到 `docs/openspec/specs/`。
3. **后续业务 change**：各自 proposal 引用本 change 的 capability spec，不得重新定义 PersonUID/源头术语。

## Open Questions

| 项 | 状态 | 说明 |
| --- | --- | --- |
| PermissionSystem 对接协议 | 开放 | 留 `identity-permission` implementation change |
| 校友是否纳入在档统计 | 已暂定 | 默认不含；产品可后续 ADR 调整 |
| position 独立 change vs 合入 classification | 开放 | 实现期按团队带宽拆分 |
