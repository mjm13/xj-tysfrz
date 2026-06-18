# ADR 0002 - Pragmatic DDD

- status: accepted
- date: 2026-06-17

## Context

项目希望按 DDD 思路讨论和沉淀，但实现层面需避免过度设计。

## Decision

- 文档与讨论阶段始终使用 DDD（统一语言、上下文、领域模型）
- 代码实现按复杂度分级：
  - 简单需求：三层实现
  - 复杂需求：完整 DDD 分层
- 每个 change 的 `design.md` 必须声明分层选择及理由

## Consequences

- 保留 DDD 的分析价值
- 降低简单需求的实现成本
- 通过 design 决策记录保持可追溯性
