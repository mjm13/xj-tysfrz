---
title: 身份数据平台领域建模（统一术语 / UID / 数据接入 / 权限定位）
status: inbox
change: identity-platform-domain
owner: team
createdAt: 2026-06-19
tier: 🔴
changeType: 业务
dependsOn: —（建模优先，阻塞后端各业务 change）
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

# 涉及限界上下文（候选，建模时确认）

- `identity-master`：人员基础身份（自然人主档，多源采集，不手工新增）
- `identity-dimension`：分类 / 岗位 / 标签（在主档上挂维度）
- `org-structure`：组织机构体系
- `data-ingestion`：数据接入（源头注册 + ETL 采集，统一 QG-08）
- `permission-reconciliation`：权限对账治理（不授权，只对账 + 推送处置）
- `identity-access`：访问控制 / RBAC（替换 mock 登录，可拆独立 change）

# 关键领域概念（统一语言 · 待建模锁定）

- **自然人 / 人员基础身份**：平台唯一主档，UID 全局唯一规范（解决 QG-04）
- **采集源（DataSource）** vs **权限系统（PermissionSystem）**：拆分 QG-01 的两套"源头"
- **分类身份 / 岗位身份 / 标签身份**：挂在主档上的不同维度，与权限的关系需界定
- **对账基线 / 对账差异 / 处置推送**：权限治理核心术语（解决 Q-M5-01/02）

# 领域规则与不变量（待确认，来自 06 台账 🔴）

- 平台不手工新增/修改自然人，主档变更来自采集源（Q-M1-02）
- 变更记录需单一真相源，可追溯到来源系统与字段（Q-M1-03）
- 权限平台不执行授权，只做对账并推送源头处置（Q-M5-01/02）
- 数据查询/SQL 需有权限与安全边界（Q-M6-01，规则在此定调，实现在 data-query-service）

# 验收标准

- [ ] GIVEN 建模完成 WHEN 查阅 `docs/domain/developing/` THEN 含 context-map / ubiquitous-language / domain-model 三件套
- [ ] GIVEN UID 规范 WHEN 跨模块引用人员 THEN 有唯一且一致的标识方案（关闭 QG-04）
- [ ] GIVEN "源头"术语 WHEN 查阅统一语言 THEN 采集源与权限系统为两个明确概念（关闭 QG-01）
- [ ] GIVEN 数据接入上下文 WHEN 描述链路 THEN 源头注册 → 采集 → 主档入库闭环清晰（关闭 QG-08）
- [ ] GIVEN 权限模块定位 WHEN 查阅模型 THEN 明确"对账治理"边界，不含授权操作（关闭 Q-M5-01）
- [ ] GIVEN 14 个 🔴 阻断项 WHEN 建模产出 THEN 每项有处置结论（解决 / 推迟 / 降级）

# 流程

- 🔴 核心业务 change：完整 OpenSpec 管道
  1. `/opsx:explore` + `ddd-modeling`：补 `docs/domain/developing/*`
  2. `/opsx:propose`：proposal / design / tasks（标注业务 change，含统一语言）
  3. 本 change 偏**建模产物**，可不写应用代码；如需轻量验证再 apply
  4. `sync` → `archive` → `sync-knowledge`：提升到 `docs/domain/established/*`
- 与 `platform-module-layout`（A 线，🟡）并行

# 备注

- 本 change **不实现业务表/API**，只产出模型与决策；后端落地在被 blocks 的各 change
- 需用户参与拍板的 🔴 决策点见 `temp/06-open-questions.md` 末尾「阻断项汇总」
- 旧 OpenSpec「指标平台」描述需在建模时一并纠偏（文档漂移 P0-09）
