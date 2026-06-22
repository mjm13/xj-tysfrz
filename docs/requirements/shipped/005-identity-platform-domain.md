---
title: 身份数据平台领域建模（统一术语 / UID / 数据接入 / 权限定位）
status: shipped
change: identity-platform-domain
openspecChange: identity-platform-domain
owner: team
createdAt: 2026-06-19
shippedAt: 2026-06-20
tier: 🔴
changeType: 业务
plan: docs/domain/established/（context-map / ubiquitous-language / domain-model）
openspec: docs/openspec/changes/archive/2026-06-20-identity-platform-domain/
demoRef: docs/原始demo/（m1~m7、etl-monitor、source-maintenance）
relatedPlan: temp/06-open-questions.md（14 个 🔴 阻断项）, temp/05-demo-feature-tasks.md（T0-06）
blocks: basic-identity, classification-identity, org-structure, identity-permission, data-query-service
---

# 背景

逐页精读 demo（见 `temp/06-open-questions.md`）暴露 **14 个 🔴 阻断项**，多数不是 UI bug 而是**领域模型缺失**：术语不统一、UID 体系分裂（`NP*` vs `UID-A*`）、「源头」一词指两种东西、权限模块定位摇摆（既叫"授权矩阵"又称"不参与授权"）、数据接入链路断裂。

若不先建模直接照 demo 写表/API，会把这些矛盾固化进数据库与代码。本 change 为**核心业务 change**，走完整 OpenSpec + Superpowers，产出可追踪的领域模型，作为后端各业务 change 的统一地基。

# 业务目标

- 形成「高校综合身份数据平台」的限界上下文地图与统一语言
- 拍板平台级关键决策：UID 规范、术语、数据接入上下文、权限模块定位
- 为后续 `basic-identity` / `classification-identity` / `org-structure` / `identity-permission` / `data-query-service` 提供稳定的建模前提

# 用例 / 用户故事

1. 作为后端开发，我希望有统一的人员 UID 规范，以便跨分类/岗位/标签/权限模块关联同一自然人。
2. 作为架构，我希望「数据接入」与「权限系统对接」是两个清晰概念，以便不再混用"源头"。
3. 作为产品，我希望权限模块定位明确（对账治理 vs 授权操作），以便 m5 的功能边界不再自相矛盾。

# 涉及限界上下文（已确认）

- `identity-master`：人员基础身份
- `identity-dimension`：分类 / 岗位 / 标签
- `org-structure`：组织机构体系
- `data-ingestion`：数据接入
- `permission-reconciliation`：权限对账治理
- `data-query`：数据查询策略
- `identity-access`：访问控制（独立 change）

# 验收标准

- [x] GIVEN 建模完成 WHEN 查阅 `docs/domain/established/` THEN 含 context-map / ubiquitous-language / domain-model
- [x] GIVEN UID 规范 WHEN 跨模块引用人员 THEN 有唯一且一致的标识方案（关闭 QG-04）
- [x] GIVEN "源头"术语 WHEN 查阅统一语言 THEN 采集源与权限系统为两个明确概念（关闭 QG-01）
- [x] GIVEN 数据接入上下文 WHEN 描述链路 THEN 源头注册 → 采集 → 主档入库闭环清晰（关闭 QG-08）
- [x] GIVEN 权限模块定位 WHEN 查阅模型 THEN 明确"对账治理"边界，不含授权操作（关闭 Q-M5-01）
- [x] GIVEN 14 个 🔴 阻断项 WHEN 建模产出 THEN 每项有处置结论（解决 / 推迟 / 降级）

# 交付物

- OpenSpec：`docs/openspec/specs/identity-*`、`data-ingestion`、`permission-reconciliation`、`data-query`；`platform-shell` delta 已合并
- 领域：`docs/domain/established/*` 已回灌
- ADR：[`docs/decisions/0007-identity-platform-domain-decisions.md`](../../decisions/0007-identity-platform-domain-decisions.md)
- 归档：`docs/openspec/changes/archive/2026-06-20-identity-platform-domain/`

# 备注

- 本 change **不实现业务表/API**；后端落地在被 blocks 的各 change
- 建议下一 change：`data-ingestion` 或 `basic-identity`（见 design 实现顺序）
