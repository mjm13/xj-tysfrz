# Conventions（研发约定）

> 文中工程/流程英文简写（DDD、ADR、AC、RBAC、spike 等）含义见 `docs/glossary.md` 术语表。

## 研发方法

- 讨论与需求建模始终采用 DDD 思维
- 实现按复杂度裁剪，不做过度设计
- 新需求先进入 `docs/requirements/inbox/`，再按下方分级选择流程

## 需求分级（不一刀切）

| 档位 | 适用 | 流程 |
| --- | --- | --- |
| 🟢 轻量 | 配置/文案/样式、简单 CRUD、独立前端页面 | Plan Mode（出方案 → 人审 → 执行），计划落 `docs/plans/`，不进 OpenSpec |
| 🟡 中等 | 单一上下文常规功能、弱不变量 | Plan Mode + 可选 Superpowers |
| 🔴 核心 | 资金/权限/复杂业务规则、跨上下文、多聚合 | 完整 OpenSpec + Superpowers |

- 拿不准先降一档，超预期再升档
- 只有 🔴 才值得 OpenSpec 的持久化与同步成本（本项目无硬性审计需求，OpenSpec 仅服务核心业务知识沉淀）
- 🧪 **探针档（spike，正交于复杂度）**：链路严重不清时先做一次性、可丢弃的探路实验，落 `docs/plans/`，产出喂正式 change；spike 代码不得直接当成果交付

## 迭代切片（需求无法一次描述完时的默认姿态）

绝大部分需求无法一次描述完所有链路，也很难一次做完。默认按纵向切片推进：

- **切片优先**：工作单元是「打通一条端到端链路的薄切片」，一个 change = 一个切片，不是一个模块；change 命名切片化（如 `identity-basic-ingest-from-hr`）
- **活文档增量**：spec、数据字典、domain 文档随每个 change 逐次生长，靠 `archive → sync-knowledge` 长期累积
- **显式未知**：proposal 必含 `In Scope` / `Out of Scope` / `Open Questions & Deferred` 三段，想不清的链路显式 park，不塞进 design 假装确定
- AC 门禁只约束本切片 In Scope 的 AC，`Deferred` AC 不阻塞当前切片完成

## 人工验收说明（每次收尾必产出，三档通用）

交给 AI 编码后，人需要脱离代码按场景点验。任何档位收尾都必须输出一段验收说明，**禁止只说「已完成、测试通过」**：

- 涉及菜单 / 模块（moduleKey + 路径）
- 改了什么功能（一句话）
- 验收场景（GIVEN/WHEN/THEN，人可复现）
- 手动验证步骤（点哪里、看到什么；后端给接口示例与预期响应）
- 自动化覆盖（测试名/命令）与需人工点验项
- 本次范围外 / Deferred

**落地位置**：追加到需求文件的 `# 验收记录` 段（🟢/🟡 只有计划文档时追加到 `docs/plans/`），多切片追加不覆盖，并在收尾摘要复述。模板见 `.cursor/rules/00-workflow.mdc`「人工验收说明」专节。

## 需求收尾门禁（提交为最终操作）

一个需求**以 git commit 为最终操作**。顺序：`verify` → 文档同步（🔴：`sync → archive → sync-knowledge`）→ 需求 `shipped` + 验收记录 → **commit**。仅当 commit 成功且无本需求未提交改动时，才可启动**下一个**需求的开发。详见 `00-workflow.mdc`「需求收尾门禁」。

## 知识沉淀（与流程档位正交）

沉淀什么与走多重的流程无关。即使 🟢 改配置，命中下面任一即沉淀：

1. 业务规则 / 不变量 → `docs/domain`（ubiquitous-language / domain-model）
2. 非显而易见的决策及原因 → `docs/decisions`（ADR）
3. 数据库 / 字段业务语义、状态流转、约束理由 → `docs/domain/.../data-dictionary.md`

不沉淀到 `docs/domain`：与代码重叠的实现细节（代码即文档，必然漂移）、一次性琐碎改动、UI 壳层 / 布局 / Design Token / 前端状态管理 / 框架 API 等技术实现词。

技术或 UI 壳层 change 的知识去向：架构说明写 `docs/architecture.md`，模块追溯写 `docs/capability-map.md`，关键决策写 ADR；不得在 `context-map.md` 新建伪领域上下文，也不得在 `domain-model.md` 写“无聚合”的技术能力。

数据库三层：结构真相在 Flyway；实时读取用 MySQL MCP（只读）；业务语义沉淀到数据字典。

写入位置：🔴 先 `developing/` 归档后提升；🟢/🟡 完成后人确认直接写 `established/`。

## 真相源裁决（测试 > Spec）

- 任务完成由测试/验证裁决，不由 Spec 自洽裁决
- 测试暴露 Spec 错误时暂停，先改 design/spec 再继续，不静默跳过
- Spec 只锁 What（边界/不变量/AC），不锁 How（选型/实现/结构）
- 每条验收标准应能对应到测试

## DDD 务实裁剪

- 简单需求（CRUD/弱业务规则）：
  - 使用三层：`Controller -> Service -> Mapper`
- 复杂需求（强不变量/多聚合/复杂业务规则）：
  - 使用四层：`interfaces -> application -> domain -> infrastructure`
- 业务 / 混合 change 的 `design.md` 必须说明本次选择及理由；纯技术 / UI 壳层 change 写技术方案与裁剪理由，不虚构领域设计

## 后端约定

- Controller 仅做编排，不承载核心业务
- Service 承担业务逻辑
- Mapper 继承 BaseMapper，作为 DB 依赖边界
- 外部依赖（Feign/第三方 API）在 infrastructure 层管理
- DB 结构变更必须通过 Flyway 脚本提交
- API 契约通过 springdoc-openapi 暴露

## 测试约定（Controller 深度链路）

- Controller 深度链路扫描最大 3 层
- **DB 集成测试**：真实 H2 + Flyway `migration-test`，禁止 Mock Mapper 返回值
- 测试数据：Flyway 测试种子优先；不足时在测试类内通过真实 Mapper 插入（见 `TestDataSupport`）
- 第 3 层外部依赖（Feign/RestTemplate）必须 `@MockBean`
- 纯计算逻辑（无 DB）使用真实实例或纯 JUnit

## Flyway 双轨

| 环境 | 目录 | 数据库 |
| --- | --- | --- |
| dev / prod | `src/main/resources/db/migration/` | MySQL |
| test | `src/test/resources/db/migration-test/` | H2 |

结构变更时生产、测试侧同版本号同步维护；生产脚本已执行后不可改。

## 前端约定

- Vue3 + Element Plus
- API 模块按 capability 组织
- 优先依据 OpenAPI 保持前后端契约一致
- `.env` 管理 API Base URL

## 流程门禁

- 开工前创建独立 worktree + `change/<name>` 分支
- 进入 propose 前，统一语言术语需已登记
- 进入 apply 前，design 中的领域设计小节需完整
- 收尾二选一：
  - 完成：`sync -> archive -> sync-knowledge`
  - 放弃：`abandon-change`

## 审批闸门（Approval Gates，必须人工确认）

- 破坏性数据库变更（删表/删列/改类型/回填）
- 新外部依赖或关键三方组件引入
- 已上线能力删除（REMOVED requirements）
- 权限、密钥、安全策略变更
- 跨限界上下文的大规模重构
