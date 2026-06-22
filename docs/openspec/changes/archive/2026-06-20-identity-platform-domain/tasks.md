## 1. 领域文档与 OpenSpec 对齐

- [x] 1.1 编写 `docs/domain/developing/context-map.md`（8 上下文 + 阻断项处置）
- [x] 1.2 编写 `docs/domain/developing/ubiquitous-language.md`（PersonUID、DataSource/PermissionSystem 等）
- [x] 1.3 编写 `docs/domain/developing/domain-model.md`（聚合、不变量、后续 change 映射）
- [x] 1.4 交叉核对 developing 三件套与 `docs/openspec/changes/identity-platform-domain/specs/*` 术语一致
- [x] 1.5 更新 `docs/requirements/inbox/005-identity-platform-domain.md` frontmatter 增加 `openspecChange: identity-platform-domain`

## 2. 关键决策沉淀

- [x] 2.1 新增 ADR `docs/decisions/0007-identity-platform-domain-decisions.md`（PersonUID、源头拆分、权限对账边界、D1–D5 摘要）
- [x] 2.2 在 ADR 中引用 14 个 🔴 阻断项处置表（与 context-map 一致）

## 3. 验收与评审

- [x] 3.1 逐项勾选 inbox 005 验收标准（6 条 AC）
- [x] 3.2 确认 6 个新 capability spec 每条 Requirement 至少有一个 Scenario
- [x] 3.3 确认 platform-shell delta spec 覆盖 ModuleLayout 与 004 实现一致
- [ ] 3.4 团队评审：后续 change 顺序（建议 data-ingestion → identity-master → identity-dimension → org-structure → permission-reconciliation → data-query）

## 4. 归档与知识提升（apply 完成后执行）

- [x] 4.1 `/opsx:sync` — 将 delta specs 合并到 `docs/openspec/specs/`
- [x] 4.2 `/opsx:archive` — 归档 change `identity-platform-domain`
- [x] 4.3 `/opsx:sync-knowledge` — developing → `docs/domain/established/`；更新 established ubiquitous-language 与 platform-shell 描述
- [x] 4.4 需求 `status: shipped` + `git mv` → `docs/requirements/shipped/005-identity-platform-domain.md`

## 5. 明确不在本 change 的任务

> 以下留给后续业务 change，本 tasks 不包含 Flyway / 后端 / 前端业务实现。

- identity-master 表结构与 API → `basic-identity`
- 分类/岗位/标签 → `classification-identity` 等
- DataSource + ETL → `data-ingestion`
- 对账与 DisposalTicket → `identity-permission`
- QueryPolicy 实现 → `data-query-service`
