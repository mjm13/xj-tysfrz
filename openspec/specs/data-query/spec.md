# data-query Specification

## Purpose

数据查询（data-query）：预置表查询与 AdHocSQL 的策略约束——表级 ACL、行级范围、脱敏与审计。规则在本上下文定义，实现在 `data-query-service` change。

## Requirements

### Requirement: QueryPolicy 绑定操作者

每次数据查询 MUST 绑定 operatorId 与 QueryPolicy；QueryPolicy MUST 由 identity-access 角色与资源 scope 推导或配置。

#### Scenario: 无策略拒绝

- **WHEN** 用户发起查询但未匹配任何 QueryPolicy
- **THEN** 系统 MUST 拒绝执行

### Requirement: AdHocSQL 安全边界

AdHocSQL MUST 仅允许 SELECT；MUST 受表白名单约束；MUST 施加与操作者相关的行级 PersonUID 范围（或等价组织范围）。

#### Scenario: 禁止非 SELECT

- **WHEN** 用户提交包含 INSERT、UPDATE 或 DELETE 的 SQL
- **THEN** 系统 MUST 拒绝

#### Scenario: 表白名单

- **WHEN** 用户查询不在白名单内的表
- **THEN** 系统 MUST 拒绝

#### Scenario: 行级范围

- **WHEN** 数据治理岗以外的角色执行 SQL 查询
- **THEN** 结果 MUST 限制在该角色允许的人员/组织范围内

### Requirement: 查询审计

系统 MUST 记录每次查询的 who、when、sqlHash 与 rowCount（或等价摘要）。

#### Scenario: 审计日志

- **WHEN** 用户成功执行一次 SQL 查询
- **THEN** 系统 MUST 写入不可篡改的审计记录

### Requirement: 与对账模块语义分离

data-query 的权限相关宽表 MUST NOT 作为 permission-reconciliation 的对账权威输入。

#### Scenario: 对账不依赖查询宽表

- **WHEN** permission-reconciliation 计算差异
- **THEN** MUST 使用 GrantSnapshot，NOT data-query 宽表作为唯一依据
