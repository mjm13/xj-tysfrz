# Conventions

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

## 知识沉淀（与流程档位正交）

沉淀什么与走多重的流程无关。即使 🟢 改配置，命中下面任一即沉淀：

1. 业务规则 / 不变量 → `docs/domain`（ubiquitous-language / domain-model）
2. 非显而易见的决策及原因 → `docs/decisions`（ADR）
3. 数据库 / 字段业务语义、状态流转、约束理由 → `docs/domain/.../data-dictionary.md`

不沉淀：与代码重叠的实现细节（代码即文档，必然漂移）、一次性琐碎改动。

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
- 每个 change 的 `design.md` 必须说明本次选择及理由

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

## Approval Gates（必须人工确认）

- 破坏性数据库变更（删表/删列/改类型/回填）
- 新外部依赖或关键三方组件引入
- 已上线能力删除（REMOVED requirements）
- 权限、密钥、安全策略变更
- 跨限界上下文的大规模重构
