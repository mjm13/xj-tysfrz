# Data Dictionary (Established)

## 说明

本文件记录已归档、可作为事实引用的数据库**业务语义**。

- 结构真相在 Flyway 迁移脚本，不在此复制
- 实时结构由 MySQL MCP（只读）读取
- 本文件只记录 DDL 里看不出的「代码外知识」：字段业务含义、状态流转、约束理由
- 写入规则：🔴 经 `sync-knowledge` 从 developing 提升；🟢/🟡 完成后人确认直接写入

## 表清单

当前暂无既定条目。

## 模板

### <表名> <table_name>

- 限界上下文：`<context>`
- 业务含义：<这张表代表什么业务概念>

| 字段 | 类型 | 业务含义 | 约束/理由 |
| --- | --- | --- | --- |
| `<col>` | `<type>` | <含义> | <为什么有这约束> |

**状态流转**（如有状态字段）：

```mermaid
stateDiagram-v2
    [*] --> draft
    draft --> submitted
    submitted --> closed
```

**关键规则**：
- <如：截止时间后 status 不可回退>
