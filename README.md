# xj-zbpt

面向 OpenSpec + Superpowers 的 DDD 驱动研发工作区。

## 工作目标

- 以 DDD 思想开展需求讨论与文档沉淀
- 用 OpenSpec 管理变更提案、设计、任务与规格同步
- 用 Superpowers 技能辅助 TDD、调试、验证与评审
- 实现阶段按复杂度裁剪分层，避免过度设计

## 目录导航

- `backend/`：后端目录（Spring Boot + MyBatis-Plus + MySQL）
- `frontend/`：前端目录（Vue3 + Element Plus）
- `docs/`：需求、领域模型、架构与 ADR
- `openspec/`：OpenSpec changes/specs/config
- `.cursor/rules/`：项目规则
- `.cursor/skills/`：项目联动技能

## AI 自主研发流水线

`explore -> propose -> apply -> verify -> sync -> archive -> sync-knowledge`

推荐统一入口：`/xijia:start`（内部编排 `xijia-ops-pipeline`）
状态查看：`/xijia:status`（返回当前分级/阶段/下一步）
终止流程：`/xijia:stop`（对称收尾为 dropped）
知识回灌：`/xijia:sync-knowledge`（归档后提升 developing -> established）

- explore：`/opsx:explore`
  - 业务 / 混合 change：补齐 `docs/domain/developing/*`（可用 `ddd-modeling`）
  - 技术 change：跳过新建 domain context，必要时补 ADR 草案
- propose：`/opsx:propose`
  - 必做：先判定 change type（`business` / `technical` / `hybrid`）
  - business / hybrid：design 含领域设计小节 + 分层裁剪决策
  - technical：design 含技术方案小节 + 分层裁剪决策，不要求虚构领域模型
- apply：**openspec-superpowers-apply**（OpenSpec 管 tasks 进度，Superpowers 管 TDD/验证/评审）
- verify：verification + code-review
- sync：`/opsx:sync`
- archive：`/opsx:archive`
- sync-knowledge：
  - business / hybrid：将 `docs/domain/developing/*` 已落地内容提升到 `docs/domain/established/*`，并补 ADR
  - technical：以 ADR/工程知识为主，避免回灌伪领域条目

## Quickstart

### 后端（规划约定）

- profile：`dev` / `test`
- DB 迁移：Flyway（`src/main/resources/db/migration/`）
- API 契约：OpenAPI（`/v3/api-docs`）

### 前端（规划约定）

- 使用 `.env` 配置 API Base URL
- API 模块按 capability 与 OpenAPI 对齐

## 命名与流程约定

- 原始需求放在 `docs/requirements/inbox/`
- change/capability 命名使用 kebab-case
- ADR 使用连续编号（`0001-...`）
- worktree 目录建议：`../xj-zbpt-worktrees/<change-name>`
