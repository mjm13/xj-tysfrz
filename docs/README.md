# Docs Index

本目录是项目的全局知识中心，承载 DDD 讨论结果、需求文档、架构约定与决策记录。

## 目录说明

- `requirements/inbox/`：原始需求入口（状态 `inbox`）
- `requirements/requirements-template.md`：需求模板（DDD 风格）
- `capability-map.md`：业务能力追溯索引（模块 → 前端入口 → 后端能力 → 数据语义 → Demo）
- `domain/README.md`：领域文档导航与使用约定
- `domain/established/`：已归档既定事实（context map / language / model）
- `domain/developing/`：开发中内容（可能回滚）
- `architecture.md`：系统与分层架构总览
- `conventions.md`：研发约定与流程门禁
- `decisions/`：ADR 决策记录

## 状态约定

- 需求：`inbox -> in-change(<name>) -> shipped/dropped`
- 领域条目：`developing -> established`
- ADR：`proposed -> accepted -> superseded/deprecated`
