# AGENTS.md — AI 协作入口地图

> 本文件是**入口路由**，不是规则正文。真相源在下方各文件中，本文件只负责"从哪开始"。
> 内容若与 `.cursor/rules/00-workflow.mdc` 冲突，以后者为准。请勿把规则细节复制到这里（避免漂移）。

## 0. 第一步读什么

| 想做的事 | 先读 | 说明 |
| --- | --- | --- |
| 任何研发任务 | `.cursor/rules/00-workflow.mdc` | 总纲：需求分级、change 分类、主流程、Approval Gates（始终生效） |
| 了解项目结构与流水线 | `README.md` | 目录导航 + `explore→propose→apply→...` 全链路 |
| 研发约定与门禁 | `docs/conventions.md` | 分级表、知识沉淀三问、测试优先于 Spec |
| 系统与分层架构 | `docs/architecture.md` | 系统总览 + 后端 DDD 可裁剪分层 |
| 看不懂英文简写 | `docs/glossary.md` | 术语表：DDD/ADR/AC/RBAC/spike 等工程流程简写的中文解释 |

## 1. 统一研发流水线

```
explore → propose → apply → verify → sync → archive → sync-knowledge → commit
```

- 编排真相源：`xijia-ops-pipeline`（分级/判型/路由/收尾闭环的唯一规范，被 `/xijia:*` 命令依赖）
- **需求收尾以 `git commit` 为最终操作**；commit 且文档同步完成后，才可进入下一需求（见 `00-workflow.mdc`「需求收尾门禁」）
- 手动入口：`/xijia:start`，遵循 `xijia-ops-pipeline`
- 自动入口：`feature-pipeline` 技能（模型可发现的**薄别名**，内容委托 `xijia-ops-pipeline`）
- 状态/终止：`/xijia:status`、`/xijia:stop`

## 2. 分级与流程（先分级，再决定走多重）

| 档位 | 适用 | 流程 |
| --- | --- | --- |
| 🟢 轻量 | 配置/文案/样式、简单 CRUD、独立前端页 | Plan Mode，计划落 `docs/plans/`，不进 OpenSpec |
| 🟡 中等 | 单上下文常规功能 | Plan Mode + 可选 Superpowers |
| 🔴 核心 | 资金/权限/复杂规则/跨上下文/多聚合 | 完整 OpenSpec + Superpowers |
| 🧪 探针(spike) | 链路严重不清、需先验证可行性（正交于复杂度） | 一次性可丢弃实验，落 `docs/plans/`，产出喂正式 change；不得当成果交付 |

详见 `docs/conventions.md` 与 `00-workflow.mdc`。

> **两条默认姿态**（需求无法一次描述完时）：① 迭代切片——一个 change = 一条端到端薄切片，不是整个模块；proposal 必含 `In Scope / Out of Scope / Open Questions & Deferred`。② 人工验收说明——任何档位收尾都必须输出「改了哪个菜单/模块、什么功能、什么场景、怎么手动点验」，禁止只说「已完成」。模板见 `00-workflow.mdc`。

## 3. 关键技能（`.cursor/skills/`）

| 阶段 | 技能 |
| --- | --- |
| 端到端编排 | `xijia-ops-pipeline`（真相源）/ `feature-pipeline`（薄别名入口） |
| 领域建模（业务/混合 change） | `ddd-modeling` |
| 实现唯一入口 | `openspec-superpowers-apply`（禁止裸跑 `openspec-apply-change`） |
| 后端测试（TDD） | `backend-test` |
| 前端测试（TDD） | `frontend-test` |
| 收尾 | `sync-knowledge`（完成）/ `abandon-change`（放弃） |

## 4. 知识沉淀去向

| 内容 | 去向 |
| --- | --- |
| 业务规则 / 术语 / 领域模型 | `docs/domain/`（developing → established） |
| 非显而易见的决策及原因 | `docs/decisions/`（ADR） |
| 数据库字段业务语义 / 状态机 | `docs/domain/.../data-dictionary.md` |
| 模块级能力追溯（模块 / moduleKey / 前端入口 / 后端能力 / 表 / Demo） | `docs/capability-map.md`（需求发布节点检查） |
| 变更提案/设计/任务/规格 | `docs/openspec/`（changes / specs） |

- 数据库**结构真相**在 Flyway 迁移脚本（`backend/xj-tysfrz-business/src/main/resources/db/migration/`），不抄进 docs。
- 实时库结构通过 MySQL MCP（只读）读取。

## 5. 必须暂停并请人工确认（Approval Gates）

- 破坏性数据库变更（删表/删列/改类型/回填）
- 新增关键外部依赖
- 删除/下线已上线能力
- 权限、密钥、安全策略变更
- 跨限界上下文的大规模重构

## 6. 技术基线

- 后端：Java + Spring Boot + MyBatis-Plus + MySQL + Flyway + springdoc-openapi
- 前端：Vue 3 + TypeScript + Element Plus + Vite + Vitest
- 详细约定见 `.cursor/rules/20-backend.mdc`、`.cursor/rules/30-frontend.mdc`
