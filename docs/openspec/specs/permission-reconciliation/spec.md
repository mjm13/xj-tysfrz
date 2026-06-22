# permission-reconciliation Specification

## Purpose

权限对账治理（permission-reconciliation）：权限项主数据、对账基线、实然授权快照对比、差异识别与 DisposalTicket 处置推送。平台 **不执行授权**，只对账并推送 PermissionSystem 处置。

## Requirements

### Requirement: 不参与授权

系统 MUST NOT 提供在平台内直接授予或回收用户权限的写操作；PermissionSystem 为授权执行方。

#### Scenario: 无授权写 API

- **WHEN** 用户在对账模块操作
- **THEN** 系统 MUST NOT 暴露「开通权限」「授权矩阵编辑」类写接口

#### Scenario: UI 命名

- **WHEN** 展示人员类型与权限项的应然规则矩阵
- **THEN** UI MUST 使用「对账基线矩阵」或等价用语，MUST NOT 使用「授权矩阵」

### Requirement: 对账基线

系统 SHALL 维护 ReconciliationBaseline，表达「某类人员应然拥有某 PermissionItem」的规则集合，供对账输入。

#### Scenario: 基线只读展示

- **WHEN** 用户查看对账基线矩阵
- **THEN** 系统 MUST 只读展示规则，基线维护入口 MUST 在明确的管理用例中定义（非本 spec 实现细节）

### Requirement: 授权快照与差异

系统 SHALL 从 PermissionSystem 拉取 GrantSnapshot，与 ReconciliationBaseline 对比产生 ReconciliationDiff（含 zombie 等子类）。

#### Scenario: 差异识别

- **WHEN** 某 PersonUID 在基线下应拥有某 PermissionItem 但快照中不存在
- **THEN** 系统 MUST 记录为 ReconciliationDiff

### Requirement: DisposalTicket 处置闭环

系统 SHALL 通过 DisposalTicket 向 PermissionSystem 推送处置请求（如 RECOVER、GRANT_REQUEST），并跟踪状态。

#### Scenario: 推送状态机

- **WHEN** 用户发起推送处置
- **THEN** DisposalTicket MUST 经历 Draft → Pushed → Acknowledged | Failed 状态流转

#### Scenario: MVP 回执

- **WHEN** 外部 PermissionSystem 尚未对接
- **THEN** 系统 MAY 使用 stub Acknowledged，但 DisposalTicket 模型与 API MUST 存在

### Requirement: 与 data-query 读模型边界

「人员权限对应信息」宽表 IF 存在于 data-query，MUST 为 permission-reconciliation 的 CQRS 读投影，NOT 授权或对账的权威源。

#### Scenario: 权威源

- **WHEN** 对账模块与查询模块展示同一人员权限数据
- **THEN** 对账差异 MUST 以 GrantSnapshot 与 Baseline 比对为准，而非查询宽表
