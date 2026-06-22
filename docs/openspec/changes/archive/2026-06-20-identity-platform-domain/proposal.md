## Why

Demo 精读暴露 14 个 🔴 阻断项：UID 分裂、两套「源头」混用、数据接入链路断裂、权限模块定位自相矛盾等。若不经建模直接写表/API，会把矛盾固化进数据库。本 change 在首个后端业务 change（`basic-identity` 等）之前，统一限界上下文、术语与不变量，作为平台级领域地基。

## What Changes

- 固化 8 个限界上下文及上下游关系（context map）
- 锁定统一语言：PersonUID、DataSource、PermissionSystem、ReconciliationBaseline 等
- 定义各上下文聚合根、不变量与跨上下文引用规则
- 对 14 个 🔴 阻断项给出处置结论（13 解决 / 1 推迟）
- 产出 OpenSpec delta specs，供后续业务 change 引用
- **BREAKING（文档）**：`platform-shell` spec 从「侧栏指标平台」更新为「顶栏 + ModuleLayout」（与 004 实现对齐）

**不在本 change 范围**：业务表 Flyway、REST API、前端业务页实现、真实 RBAC 替换 Mock。

## Capabilities

### New Capabilities

- `identity-master`: 人员基础身份主档——PersonUID、多源投影、冲突裁定、单一 ChangeLog
- `identity-dimension`: 身份维度——分类树/映射/未映射队列、岗位映射治理、自定义标签
- `org-structure`: 组织机构——SYSU_ORG 权威编码、组织映射与花名册投影
- `data-ingestion`: 数据接入——DataSource 注册、IngestionJob/Run、注册→采集→入库闭环
- `permission-reconciliation`: 权限对账治理——对账基线、差异、DisposalTicket；**不执行授权**
- `data-query`: 数据查询策略——QueryPolicy、AdHocSQL 安全边界与审计

### Modified Capabilities

- `platform-shell`: 主布局从「232px 侧栏指标平台」更新为「顶栏 + ModuleLayout + 首页无侧栏」

## Impact

| 区域 | 影响 |
| --- | --- |
| `docs/domain/developing/*` | 三件套已写入，归档后提升至 established |
| `openspec/specs/*` | 新增 6 个 capability spec；platform-shell delta |
| `backend/` | 无代码变更（本 change） |
| `frontend/` | 无代码变更（004 已落地壳层） |
| 后续 change | `basic-identity`、`classification-identity`、`org-structure`、`identity-permission`、`data-ingestion`、`data-query-service` 依赖本模型 |

## 来源需求

- `docs/requirements/inbox/005-identity-platform-domain.md`
- `docs/domain/developing/`（explore 产出，2026-06-20）
- `temp/06-open-questions.md`（🔴 阻断项台账）

## Change 类型

**业务 change**（纯领域建模与决策，无应用代码）
