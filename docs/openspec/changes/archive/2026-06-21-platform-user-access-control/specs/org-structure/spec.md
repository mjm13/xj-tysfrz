# org-structure Specification

## Purpose

本 delta 为 identity-access 提供最小 org_node 种子，满足 DepartmentRef 与 DataScopeResolver 依赖。

## ADDED Requirements

### Requirement: 最小组织节点种子

系统 SHALL 在 Flyway 迁移中创建 org_node 表并导入 SYSU_ORG 权威组织码种子数据，供 identity-access 的 DepartmentRef 校验与 DataScopeResolver 组织树遍历。

#### Scenario: 种子数据可查询

- **WHEN** identity-access 迁移完成后查询 org_node
- **THEN** MUST 存在非空组织树且根节点 code 与 SYSU_ORG mock 一致（如 `SYSU`）

#### Scenario: DepartmentRef 可引用

- **WHEN** 管理员为用户设置 departmentRef
- **THEN** 所选 code MUST 存在于 org_node 表

#### Scenario: 子孙节点可解析

- **WHEN** DataScopeResolver 对某 departmentRef 计算 OWN_DEPT_AND_SUB
- **THEN** MUST 能基于 org_node.parent_code 关系解析全部子孙节点

## 说明

本 ADDED 不替代完整 org-structure change（OrgMapping、OrgRoster、树编辑持久化仍留给后续 `org-structure` change）。
