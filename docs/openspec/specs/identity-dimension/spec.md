# identity-dimension Specification

## Purpose

身份维度（identity-dimension）：人员分类、岗位、自定义标签三类维度能力，均通过 PersonUID 挂载于 identity-master 主档之上。

## Requirements

### Requirement: 分类树权威编码

ClassificationTree MUST 使用与标准分类编码一致的 ClassificationNode.code（如 SYSU_CLASSIFICATION），并支持至少 5 级深度。

#### Scenario: 节点深度

- **WHEN** 标准分类存在 5 级节点
- **THEN** 系统 MUST 允许在树中表达该节点，不得限制为 4 级

#### Scenario: 有挂载节点不可物理删除

- **WHEN** ClassificationNode 上存在 PersonClassification 挂载
- **THEN** 系统 MUST NOT 物理删除该节点，仅允许停用

### Requirement: 分类映射与未映射队列

系统 SHALL 维护 MappingRuleSet（源字段值 → ClassificationNode）与统一的 UnmappedRecord 队列。

#### Scenario: 未映射发现

- **WHEN** 采集值无法匹配任何 MappingRule
- **THEN** 系统 MUST 创建 UnmappedRecord，状态为 Detected

#### Scenario: 未映射闭环

- **WHEN** 治理人员为 UnmappedRecord 配置映射并完成归类
- **THEN** UnmappedRecord 状态 MUST 变为 Mapped，且 MUST 产生 PersonClassification 或等价挂载

#### Scenario: 主站与 admin 共享队列

- **WHEN** 主站与 admin 视图展示未映射列表
- **THEN** 两者 MUST 读取同一 UnmappedQueue 真相源，不得维护独立 mock 数据集

### Requirement: 岗位目录只读与映射治理写

PositionCatalog（岗位列表）MUST 为只读投影；岗位映射的写操作 MUST 仅通过 PositionMappingBatch 治理流程。

#### Scenario: 列表只读

- **WHEN** 用户浏览岗位身份主列表
- **THEN** 页面 MUST NOT 提供直接编辑标准岗位的入口

#### Scenario: 映射批次入库

- **WHEN** 用户提交 PositionMappingBatch 且所有行状态为 Confirmed
- **THEN** 系统 MUST 持久化映射并发布事件刷新 PositionCatalog 读模型

#### Scenario: 拒绝无效行入库

- **WHEN** PositionMappingBatch 包含 invalid 或 pending 行
- **THEN** 系统 MUST NOT 将其计入 savedCount 或持久化为有效映射

### Requirement: 岗位映射结构化单位

PositionMapping MUST 使用结构化 orgUnitCode 引用组织单位；MUST NOT 使用 `/` 分隔符拆分单位名字符串生成映射组合。

#### Scenario: 禁止斜杠拆分

- **WHEN** 源数据单位名为「党委办公室/校长办公室」合法单实体
- **THEN** 系统 MUST NOT 将其拆成两个单位做笛卡尔积映射

### Requirement: 自定义标签

TagGroup 与 TagAssignment MUST 通过 PersonUID 关联人员；可选启用岗位约束时 MUST 校验 PersonPosition 存在。

#### Scenario: 标签引用主档

- **WHEN** 创建 TagAssignment
- **THEN** MUST 引用有效 PersonUID

#### Scenario: 岗位约束可选

- **WHEN** TagGroup 配置「基于岗位身份」且用户添加人员
- **THEN** 系统 MUST 校验该 PersonUID 存在对应 PersonPosition
