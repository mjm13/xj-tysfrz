# ADR 0007: 身份数据平台领域建模关键决策

- **Status:** accepted
- **Date:** 2026-06-20
- **Change:** identity-platform-domain
- **Supersedes:** —
- **Related:** ADR 0006（前端 ModuleLayout）

## Context

Demo 精读产生 14 个 🔴 阻断项，需在首个后端业务 change 前锁定平台级领域决策。详见 `openspec/changes/archive/2026-06-20-identity-platform-domain/` 与 `docs/domain/established/`。

## Decision

### D1 — PersonUID

- 格式：`UID` + 10 位零填充数字（如 `UID0000123456`）
- 源系统键独立：`{dataSourceId}:{sourceNativeId}`

### D2 — 源头术语拆分

- **DataSource（采集源）**：人事、教务等向平台供数
- **PermissionSystem（权限系统）**：校园网、图书馆等执行授权
- UI/文档禁止无限定词使用「源头」

### D3 — 主档写策略

- PersonRecord 禁止 UI/API 自由 PATCH
- 变更来自 data-ingestion 或 ConflictCase 裁定
- 单一 ChangeLog 审计链

### D4 — 权限模块定位

- permission-reconciliation：**对账治理 only**
- UI 使用「对账基线矩阵」，禁止平台侧授权写操作
- 处置通过 DisposalTicket 推送 PermissionSystem

### D5 — 数据接入单上下文

- source-maintenance + etl-monitor + m1 内嵌 sync → **data-ingestion**
- 注册 → IngestionJob → IngestionRun → 领域事件入库

### D6 — 组织编码权威

- OrgNode.code 必须以 SYSU_ORG 为权威，禁止 demo 假 code

## 🔴 阻断项处置

| 编号 | 处置 |
| --- | --- |
| QG-01 | 解决 — D2 |
| QG-04 | 解决 — D1 |
| QG-08 | 解决 — D5 |
| Q-M1-02 | 解决 — D3 |
| Q-M1-03 | 解决 — D3 ChangeLog |
| Q-M2-01 | 解决 — 004 子路由 |
| Q-M2-02 | 解决 — UnmappedQueue |
| Q-M3-01 | 解决 — 映射治理写/列表读 |
| Q-M3-02 | 解决 — 结构化 orgUnitCode |
| Q-M4-01 | 解决 — D6 |
| Q-M5-01 | 解决 — D4 |
| Q-M5-02 | 解决 — DisposalTicket |
| Q-M6-01 | 解决 — QueryPolicy（实现推迟） |
| Q-SRC-01 | 推迟 — UI 重写 |

## Consequences

- 后续 6+ 业务 change MUST 引用 OpenSpec capability specs，不得重定义术语
- platform-shell established 文档需在 archive 后 sync-knowledge 更新

## Alternatives considered

- 平台内直接授权：与 demo 文案及高校权限分散现状冲突，拒绝
- 保留 demo 双 UID 体系：跨模块无法关联，拒绝
