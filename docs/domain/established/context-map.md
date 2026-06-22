# Context Map (Established)

## 说明

本文件记录已归档、可稳定复用的限界上下文关系。

## 上下文清单

### platform-shell（平台壳层）

- status: active
- 来源：change `frontend-app-shell`（2026-06-18）、`platform-module-layout`（2026-06-20 布局纠偏）、`platform-user-access-control`（2026-06-21 后端认证集成）
- 业务含义：登录页、全局路由、MainLayout（首页）/ ModuleLayout（业务模块）、Design Token；登录对接 identity-access JWT
- 对应 spec：`openspec/specs/platform-shell/spec.md`

### identity-access（平台访问控制）

- status: active
- 来源：change `platform-user-access-control`（2026-06-21）
- 业务含义：InteractiveUser 账号、RBAC、DataScope、JWT 认证、OperatorContext；与 PersonUID 无关
- 对应 spec：`openspec/specs/identity-access/spec.md`
- 替代：`identity-access-mock`（已退役）

### identity-access-mock（Mock 身份与访问）

- status: superseded
- 来源：change `frontend-app-shell`（2026-06-18）；由 `platform-user-access-control`（2026-06-21）退役
- 对应 spec：`openspec/specs/identity-access-mock/spec.md`（无活跃 Requirement）

### identity-master（人员基础身份）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）
- 业务含义：自然人主档、PersonUID、多源投影、冲突裁定、ChangeLog
- 对应 spec：`openspec/specs/identity-master/spec.md`

### identity-dimension（身份维度）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）
- 业务含义：分类 / 岗位 / 自定义标签，挂载于 PersonUID
- 对应 spec：`openspec/specs/identity-dimension/spec.md`

### org-structure（组织机构）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）；最小 `org_node` 种子由 `platform-user-access-control`（2026-06-21）落地
- 对应 spec：`openspec/specs/org-structure/spec.md`

### data-ingestion（数据接入）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）
- 业务含义：DataSource 注册、IngestionJob/Run、注册→采集→入库
- 对应 spec：`openspec/specs/data-ingestion/spec.md`

### permission-reconciliation（权限对账治理）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）
- 业务含义：对账基线、差异、DisposalTicket；**不执行授权**
- 对应 spec：`openspec/specs/permission-reconciliation/spec.md`

### data-query（数据查询）

- status: active
- 来源：change `identity-platform-domain`（2026-06-20）
- 业务含义：QueryPolicy、AdHocSQL 安全边界（实现在 `data-query-service` change）
- 对应 spec：`openspec/specs/data-query/spec.md`

## 关系图

```mermaid
flowchart TB
    subgraph shell ["壳层"]
        PS[platform-shell]
        IA[identity-access]
    end

    subgraph ingestion ["data-ingestion"]
        DS[DataSource]
        ETL[IngestionJob / Run]
    end

    subgraph core ["核心身份"]
        IM[identity-master]
        ID[identity-dimension]
        ORG[org-structure]
    end

    subgraph perm ["permission-reconciliation"]
        BL[ReconciliationBaseline]
        DIS[DisposalTicket]
    end

    subgraph query ["data-query"]
        QV[QueryView]
    end

    IA --> PS
    IA -->|DepartmentRef / DataScope| ORG
    PS --> core
    PS --> ingestion
    PS --> perm
    PS --> query

    DS --> ETL
    ETL --> IM
    ETL --> ID
    ETL --> ORG

    IM --> ID
    IM --> ORG

    PSys[PermissionSystem 外部] -->|GrantSnapshot| perm
    DIS -->|PushDisposal| PSys

    IM --> QV
    ID --> QV
    ORG --> QV
    perm --> QV
```

## 集成模式（摘要）

| 上游 | 下游 | 模式 |
| --- | --- | --- |
| data-ingestion | identity-master | ACL + 领域事件 |
| identity-master | identity-dimension | OHS（PersonUID） |
| permission-reconciliation | PermissionSystem | 防腐层 + 处置推送 |
| identity-access | 各 API | 横切 RBAC + DataScope |

完整关系与阻断项处置见归档 change `identity-platform-domain` 与 ADR 0007。
