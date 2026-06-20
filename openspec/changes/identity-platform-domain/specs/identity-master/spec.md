# identity-master Specification

## Purpose

人员基础身份（identity-master）：自然人主档、平台级 PersonUID、多采集源投影、冲突裁定与单一变更审计链。平台不手工随意修改主档属性。

## ADDED Requirements

### Requirement: PersonUID 全局唯一

系统 SHALL 为每个自然人分配唯一的 PersonUID，格式为 `UID` 前缀加 10 位数字（零填充），作为全平台跨模块引用人员的唯一标识。

#### Scenario: 跨模块引用同一人员

- **WHEN** 分类、岗位、标签或权限模块引用某自然人
- **THEN** MUST 使用同一 PersonUID，不得使用源系统本地 ID 或 demo 遗留的 `NP*` / `UID-A*` 格式

#### Scenario: PersonUID 不可变更

- **WHEN** 人员主档已创建并分配 PersonUID
- **THEN** 系统 MUST NOT 允许修改该 PersonUID

### Requirement: 多源投影

系统 SHALL 为每个 PersonRecord 维护来自各 DataSource 的 SourceProjection，每条投影 MUST 携带 `dataSourceId` 与 `sourceRecordKey`。

#### Scenario: 多源采集写入

- **WHEN** data-ingestion 从两个人事相关源写入同一自然人不同字段
- **THEN** PersonRecord MUST 保留两条 SourceProjection，并分别记录来源

### Requirement: 主档只读（UI/API）

系统 MUST NOT 提供直接编辑 PersonRecord 属性的自由 PATCH 接口或 UI 表单；主档属性变更仅来自采集写入或 ConflictCase 裁定。

#### Scenario: 拒绝自由编辑

- **WHEN** 用户尝试通过「编辑主档」类操作修改姓名或证件号
- **THEN** 系统 MUST 拒绝，并引导至冲突裁定或采集源修正流程

#### Scenario: 采集驱动更新

- **WHEN** IngestionRun 成功写入某 DataSource 的人员增量
- **THEN** 对应 SourceProjection 与合并视图 MUST 更新，并产生 ChangeLog 条目

### Requirement: 冲突裁定

当多个 SourceProjection 对同一属性提供冲突值时，系统 SHALL 创建 ConflictCase，状态为 Open，直至裁定完成。

#### Scenario: 冲突检测

- **WHEN** 两个采集源对同一 PersonUID 的同一属性给出不同值
- **THEN** 系统 MUST 创建 ConflictCase 且主档合并视图 MUST NOT 静默覆盖

#### Scenario: 裁定写入审计

- **WHEN** 治理人员完成 ConflictCase 裁定并选定 authoritativeSource
- **THEN** 系统 MUST 将结论写入 ChangeLog，ConflictCase 状态变为 Resolved

### Requirement: 单一 ChangeLog

系统 SHALL 维护唯一的 ChangeLog 作为人员主档及相关裁定的审计真相源；禁止并行独立 mock 或局部时间轴。

#### Scenario: 审计可追溯

- **WHEN** 用户查询某 PersonUID 的变更记录
- **THEN** 系统 MUST 返回来自 ChangeLog 的完整链路，包含 ingestionRunId 或 conflictCaseId 关联

### Requirement: 在档身份口径

「在档身份人员」统计 MUST 基于 PersonRecord 状态 ACTIVE，且默认不包含校友与纯访客类型（与平台首页统计口径一致）。

#### Scenario: 首页统计一致

- **WHEN** 平台首页展示在档人数
- **THEN** 计数 MUST 与 identity-master ACTIVE 主档集合一致，不含校友/纯访客
