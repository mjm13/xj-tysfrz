# data-ingestion Specification

## Purpose

数据接入（data-ingestion）：采集源（DataSource）注册、采集任务（IngestionJob）与运行（IngestionRun），实现「注册 → 采集 → 事件入库」闭环。本上下文 MUST NOT 与 PermissionSystem 混称「源头」。

## ADDED Requirements

### Requirement: DataSource 与 PermissionSystem 分离

系统 MUST 将 DataSource（向平台供给人员/组织/分类数据的业务系统）与 PermissionSystem（执行授权的外部系统）建模为不同概念；文档与 UI MUST NOT 混用「源头」一词而不加限定。

#### Scenario: 采集源注册

- **WHEN** 用户在源头维护页注册人事或教务系统
- **THEN** 创建的是 DataSource，NOT PermissionSystem

#### Scenario: 权限系统对接

- **WHEN** 权限对账模块配置校园网或图书馆系统
- **THEN** 引用的是 PermissionSystem，NOT DataSource

### Requirement: 注册到采集的消费链

每个已注册且启用的 DataSource MUST 可被至少一个 IngestionJob 引用；禁止「已注册但 ETL 不消费」的配置断裂。

#### Scenario: 新源可采集

- **WHEN** 治理人员完成 DataSource 注册并启用
- **THEN** 系统 MUST 允许创建或关联 IngestionJob 对该源执行采集

#### Scenario: 运行产生领域事件

- **WHEN** IngestionRun 成功完成
- **THEN** 系统 MUST 发布可供 identity-master / identity-dimension / org-structure 消费的领域事件（如 RecordsUpserted）

### Requirement: IngestionJob 与 IngestionRun

系统 SHALL 区分 IngestionJob（调度定义）与 IngestionRun（单次执行实例），Run MUST 记录成功/失败与增量统计。

#### Scenario: 运行历史

- **WHEN** 用户查看 ETL 监控页某任务
- **THEN** 系统 MUST 展示该 Job 的历史 Run 记录

### Requirement: 人员大类绑定策略

DataSource 注册 SHOULD 遵循「每人员大类绑定一个主 DataSource」；同一类下的补充源 MUST 标记为 Supplemental 角色。

#### Scenario: 学生双源

- **WHEN** 本科生与研究生分属教务系统与研究生系统
- **THEN** 系统 MUST 允许在同一人员大类下配置主源与补充源，而非违反「一大类一系统」语义

### Requirement: 收敛 demo 三套同步 UI

m1 内嵌 sync/source 视图 MUST 移除或重定向至统一的 data-ingestion 模块；不得与 source-maintenance、etl-monitor 并行维护独立配置真相源。

#### Scenario: 统一入口

- **WHEN** 用户从人员基础身份模块访问「同步」或「源头」能力
- **THEN** MUST 导航至 data-ingestion 统一 UI，而非独立 hash 视图
