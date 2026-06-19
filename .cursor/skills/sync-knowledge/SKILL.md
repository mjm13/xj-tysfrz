---
name: sync-knowledge
description: 归档后知识回灌（spec/change -> docs/domain/established + ADR）
---

# 目标

在 change 完成归档后，将增量知识沉淀到长期文档，形成迭代闭环。

# 输入

- change 名称
- 该 change 的 design、tasks、delta specs

# 执行步骤

1. 读取已归档 change 的设计与 specs 变更
2. 将 `docs/domain/developing/*` 中本次 change 已落地内容提升到 `docs/domain/established/*`
3. 清理 `developing` 中被回滚或失效的临时条目
4. 更新 established 的 context map、domain model、ubiquitous language（仅保留已确认事实）
5. 如有架构或流程决策，新增 ADR
6. 若发现设计与实现偏差，记录待修正事项
7. 将对应需求 `status` 置 `shipped`，并用 `git mv` 移到 `docs/requirements/shipped/`；移动后 Grep 修正引用该路径的文档，避免死链

# 约束

- 未归档 change 的内容不得写入 `docs/domain/established/*`
- 回灌优先做增量修改，避免重写整份文档
- 需求文档物理位置须与 `status` 一致（见 `00-workflow.mdc` 文档状态模型）
