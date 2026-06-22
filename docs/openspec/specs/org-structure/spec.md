# org-structure Specification

## Purpose

组织机构（org-structure）：组织树、源系统组织映射、组织维度花名册投影。组织编码以 SYSU_ORG 为权威来源。

## Requirements

### Requirement: 组织编码权威

OrgNode.code MUST 来自权威组织码表（SYSU_ORG）；系统 MUST NOT 接受 demo 遗留假编码（如 `U00`、`ADM`）作为持久化组织节点。

#### Scenario: 映射使用真实 code

- **WHEN** 创建 OrgMapping 将源组织码映射到平台组织
- **THEN** target MUST 为 SYSU_ORG 中存在的 OrgNode.code

#### Scenario: 花名册与树一致

- **WHEN** 用户选中某 OrgNode 查看花名册
- **THEN** 系统 MUST 能解析该 code 对应节点，不得出现「节点不存在」因编码体系不一致导致

### Requirement: 组织树变更持久化

组织树的增删改 MUST 持久化并写入 ChangeLog；MUST NOT 使用仅 alert 占位、刷新即丢失的编辑方式。

#### Scenario: 树编辑持久化

- **WHEN** 用户完成组织节点的新增或移动并确认
- **THEN** 变更 MUST 写入存储并在刷新后仍可读取

### Requirement: 组织映射可配置

系统 SHALL 提供 OrgMapping，将 DataSource 的组织编码映射到 OrgNode.code。

#### Scenario: 映射维护

- **WHEN** 用户在组织模块配置某采集源的组织映射
- **THEN** 系统 MUST 持久化映射关系并可在组织详情中展示

### Requirement: 组织层级表达

组织树筛选与展示 MUST 支持 L1 至 L5 层级（与 SYSU_ORG 数据一致）。

#### Scenario: 深层级节点

- **WHEN** 组织数据存在 level 4 或 5 节点
- **THEN** 系统 MUST 允许浏览与筛选，不得限制为 L1–L3

### Requirement: 组织花名册投影

OrgRoster MUST 为只读投影，列出某 OrgNode 下在档 PersonUID 集合，由 ingestion 与维度关系生成。

#### Scenario: 花名册查询

- **WHEN** 用户查看某组织节点花名册
- **THEN** 系统 MUST 返回与该 orgNodeCode 关联的有效 PersonUID 列表

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

### Requirement: 组织节点平台管理 API

系统 SHALL 提供平台管理侧 org_node 维护 API（懒加载树 + 创建/更新），受 `admin:departments:*` 保护。

#### Scenario: 查询根节点

- **WHEN** 持有 admin:departments:read 的用户 GET `/api/admin/org-nodes/roots`
- **THEN** MUST 返回 parent_code 为空的 org 节点列表

#### Scenario: 懒加载子节点

- **WHEN** 持有 admin:departments:read 的用户 GET `/api/admin/org-nodes/children?parentCode=SYSU`
- **THEN** MUST 返回 parent_code=SYSU 的直接子节点

#### Scenario: 创建组织节点

- **WHEN** 持有 admin:departments:write 的用户 POST 合法 code/name/parentCode
- **THEN** MUST 持久化 org_node；parent MUST 存在；level MUST 正确派生

#### Scenario: 更新组织节点

- **WHEN** 持有 admin:departments:write 的用户 PUT 更新 name 或 parentCode
- **THEN** MUST 持久化；parent 变更 MUST NOT 成环

#### Scenario: 无效 parent 拒绝

- **WHEN** parentCode 不存在或会导致环
- **THEN** MUST 返回业务错误并拒绝保存

#### Scenario: 无权限拒绝

- **WHEN** 无 admin:departments:read 的用户请求 roots/children
- **THEN** MUST 返回 403

### Requirement: 组织节点管理界面

前端 SHALL 提供 `/admin/departments`：ElTree 懒加载展示 org_node，支持新建与编辑（无删除）。

#### Scenario: 新建后用户表单可见

- **WHEN** 管理员创建新 org 节点且平台用户页拉取 org 列表
- **THEN** 新 code MUST 出现在部门选项中
