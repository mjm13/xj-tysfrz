# 统一语言（Ubiquitous Language，已归档）

## 说明

本文件记录已归档、已稳定复用的统一语言术语。

## 访问控制（identity-access）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| Principal（主体） | identity-access | 可被认证与授权的实体抽象 | InteractiveUser |
| InteractiveUser（交互式用户） | identity-access | 人类操作者平台账号 | 含凭证、角色、部门、DataScope |
| PlatformUserId | identity-access | 平台用户唯一标识，与 PersonUID **无关** | UUID 或业务编码 |
| Role（角色） | identity-access | 权限集合，分配给用户 | 如 `ADMIN` |
| 系统角色（System Role） | identity-access | 平台内建、受保护的角色：权限不可修改、角色不可删除；区别于可自由编辑的自定义角色 | ADMIN（系统角色）vs GOVERNANCE（自定义） |
| Permission（权限项） | identity-access | 对模块/操作的访问授权 | `module:action` |
| DepartmentRef（部门归属） | identity-access | 用户所属 OrgNode 的 code | 引用 org-structure |
| DataScope（数据范围） | identity-access | 用户可见数据的部门口径档位 | GLOBAL / OWN_DEPT / OWN_DEPT_AND_SUB |
| ScopedDeptSet（可见部门集） | identity-access | DataScope + 组织树派生的 OrgNode code 集合 | 运行时计算 |
| OperatorContext（操作者上下文） | identity-access | 请求级：userId、roles、permissions、scopedDeptCodes | 请求级上下文 |
| NavigationMenu（导航菜单） | identity-access | 平台 UI 入口树节点 | `platform_menu` |
| MenuPermission（菜单权限关联） | identity-access | 菜单与 Permission 的一对多子表记录 | `platform_menu_permission` |
| NavigationTree（运行时导航树） | platform-shell | 按用户 permissions 过滤后的菜单投影 | GET `/api/navigation` |
| AuthSource（认证来源） | identity-access | 凭证校验来源 | SELF_BUILT |
| AccessToken（访问令牌） | identity-access | 登录成功后返回的访问凭证 | |
| AuthProvider（认证提供者） | identity-access | 可插拔认证实现 | SelfBuiltAuthProvider |

## 平台级标识

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonUID（人员 UID） | identity-master | 平台级自然人唯一标识 | `UID` + 10 位数字，如 `UID0000123456` |
| SourceRecordKey（源记录键） | data-ingestion | 采集源内原始主键 | `{dataSourceId}:{sourceNativeId}` |
| 在档身份人员 | identity-master | 有效主档人员，默认不含校友/纯访客 | `PersonRecord.status = ACTIVE` |

## 采集 vs 权限

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| DataSource（采集源） | data-ingestion | 向平台供数的业务系统 | 人事、教务等 |
| PermissionSystem（权限系统） | permission-reconciliation（外部） | 执行授权的业务系统 | 校园网、图书馆等；**不是** DataSource |
| IngestionJob（采集任务） | data-ingestion | 从 DataSource 拉数的调度单元 | |
| IngestionRun（采集运行） | data-ingestion | 任务的一次执行实例 | |

## 人员主档（identity-master）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PersonRecord（人员主档） | identity-master | 自然人主数据聚合根 | 不可绕过主档治理自由编辑 |
| SourceProjection（源投影） | identity-master | 某 DataSource 的字段快照 | |
| ConflictCase（冲突个案） | identity-master | 多源属性冲突待裁定 | |
| ChangeLog（变更记录） | identity-master | 唯一审计链 | |

## 身份维度（identity-dimension）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| ClassificationNode（分类节点） | identity-dimension | 人员分类树节点 | `SYSU_CLASSIFICATION` |
| PersonClassification | identity-dimension | PersonUID 挂载分类 | |
| MappingRule（映射规则） | identity-dimension | 源值 → 分类节点 | |
| UnmappedRecord（未映射记录） | identity-dimension | 待治理未映射项 | UnmappedQueue |
| PositionCatalog（岗位目录） | identity-dimension | 标准岗位（只读投影） | |
| PositionMapping（岗位映射） | identity-dimension | 源描述 → 标准岗位（治理写） | |
| TagGroup / TagAssignment | identity-dimension | 自定义标签组与标注 | |

## 组织机构（org-structure）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| OrgNode（组织节点） | org-structure | 组织树节点 | code 来自 `SYSU_ORG` |
| OrgMapping（组织映射） | org-structure | 源组织码 → OrgNode | |
| OrgRoster（组织花名册） | org-structure | 组织下 PersonUID 列表（投影） | |

## 权限对账（permission-reconciliation）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| PermissionItem（权限项） | permission-reconciliation | 可对账权限类型主数据 | |
| ReconciliationBaseline（对账基线） | permission-reconciliation | 应然授权规则 | 对账基线矩阵 |
| GrantSnapshot（授权快照） | permission-reconciliation | 从 PermissionSystem 拉取的实然状态 | 只读 |
| ReconciliationDiff（对账差异） | permission-reconciliation | 基线 vs 快照不一致 | |
| DisposalTicket（处置单） | permission-reconciliation | 推送 PermissionSystem 的工单 | |

## 数据查询（data-query）

| term | context | definition | codeHint |
| --- | --- | --- | --- |
| QueryPolicy（查询策略） | data-query | 表 ACL、行级范围、审计 | |
| AdHocSQL（即席 SQL） | data-query | 受策略约束的 SELECT | |

## 命名禁忌

| 禁用 | 统一用语 |
| --- | --- |
| 无限定词「源头」 | 采集源 / 权限系统 |
| 授权矩阵 | 对账基线矩阵 |
| 用户部门自建树 | DepartmentRef 引用 OrgNode |
| 在前端 alone 做数据范围过滤 | OperatorContext + 后端 ScopedDeptSet |
| 平台用户 = PersonUID | 平台用户（InteractiveUser）≠ 自然人（PersonUID） |
| 在 menu 表单字段存 permission | 必须用 `platform_menu_permission` 子表 |

详细术语与阻断项映射见 ADR [`0007-identity-platform-domain-decisions.md`](../../decisions/0007-identity-platform-domain-decisions.md)。
