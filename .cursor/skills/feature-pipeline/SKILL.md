---
name: feature-pipeline
description: 端到端功能研发编排入口（DDD + OpenSpec + Superpowers）
---

# 目标

将用户的“我要做 X”请求，按统一流水线推进到“完成归档”或“放弃收尾”。

# 输入

- 需求描述（可来自 `docs/requirements/inbox/`）
- 可选 change 名称（未提供时由描述生成 kebab-case）

# Change 分类（先判定，再执行）

与 `.cursor/rules/00-workflow.mdc` 一致，每个 change 先归类：

| 类型 | 领域文档 | design 要求 |
| --- | --- | --- |
| **业务** | 必须更新 `docs/domain/developing/*` | 领域设计小节 + 分层裁剪 |
| **技术** | 跳过新建 context；对接已有上下文时仅更新描述 | 技术方案小节 + 分层裁剪；关键决策写 ADR |
| **混合** | 仅建模业务部分 | 两者兼有 |

技术 change 示例：`springboot-backend-skeleton`（工程骨架、日志、Actuator）——不写 `logging` / `api-foundation` 等新 context。

# 执行步骤

0. **需求分级（先分级，再决定走多重的流程）** —— 与 `00-workflow.mdc` 一致：
   - 🟢 轻量（配置/文案/样式、简单 CRUD、独立前端页面）→ **Plan Mode**：出方案 → 人审 → 执行，计划落 `docs/plans/`；**到此即止，不进下面步骤**
   - 🟡 中等（单一上下文常规功能）→ Plan Mode + 可选 Superpowers；需沉淀领域知识才补 `docs/domain/developing/*`
   - 🔴 核心（资金/权限/复杂规则/跨上下文/多聚合）→ 走下面完整流水线
   - 拿不准先降一档，超预期再升档

1. 建立隔离环境（🔴 档）：
   - 使用 worktree + `change/<name>` 分支
2. 探索阶段：
   - 调用 `/opsx:explore`
   - **业务 / 混合 change**：调用 `ddd-modeling` 更新 `docs/domain/developing/*`
   - **技术 change**：跳过 `ddd-modeling`；必要时在 `docs/adr/` 写技术决策草案
3. 提案阶段：
   - 调用 `/opsx:propose <name>`
   - 确认 proposal 已标注 change 类型
   - 检查 design：
     - 业务 / 混合：领域设计小节 + 分层裁剪决策
     - 技术：技术方案小节（栈选型、目录结构、横切能力）+ 分层裁剪决策
4. 实现阶段：
   - 调用 **openspec-superpowers-apply**（强制 OpenSpec 任务跟踪 + Superpowers TDD/验证/评审）
   - 不要裸用 `openspec-apply-change` 而不加载 Superpowers
5. 验证阶段：
   - 执行测试与验证
   - 需要时进行代码评审
6. 收尾阶段（二选一）：
   - 完成：`/opsx:sync` -> `/opsx:archive` -> `sync-knowledge`
   - 放弃：`abandon-change`

# 门禁

- propose 前 proposal 必须标注 change 类型（业务 / 技术 / 混合）
- propose 前统一语言：**业务 / 混合** 必须登记术语；**技术** 可跳过
- apply 前 design 按 change 类型满足对应小节（见上表）
- 技术 change 关键决策写入 `docs/adr/`，禁止在 `context-map` 新建伪 context
- 未归档前禁止写入 `docs/domain/established/*`
- 命中 approval gates 必须先请求用户确认
