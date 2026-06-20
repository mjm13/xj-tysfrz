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
