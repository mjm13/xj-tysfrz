---
name: sync-knowledge
description: 归档后知识回灌（spec/change -> docs/domain/established + ADR + capability map）
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
5. 检查数据库业务语义：新增/变更表、字段含义、状态机、约束理由时，更新 `docs/domain/established/data-dictionary.md`；如只需要关系级 ER 图，仅画表间业务关系，不复制 DDL
6. 检查能力追溯索引：当 change 新增、下线、重命名业务模块，变更前端 `moduleKey`，或新增模块相关业务表时，更新 `docs/capability-map.md`
7. 如有架构或流程决策，新增 ADR
8. 若发现设计与实现偏差，记录待修正事项
9. 将对应需求 `status` 置 `shipped`，并用 `git mv` 移到 `docs/requirements/shipped/`；移动后 Grep 修正引用该路径的文档，避免死链

# 约束

- 未归档 change 的内容不得写入 `docs/domain/established/*`
- 回灌优先做增量修改，避免重写整份文档
- `docs/capability-map.md` 只记录模块级指针，不复制子菜单、路由明细或表结构；真相分别在前端路由/菜单、OpenSpec specs 与 Flyway
- 需求文档物理位置须与 `status` 一致（见 `00-workflow.mdc` 文档状态模型）
