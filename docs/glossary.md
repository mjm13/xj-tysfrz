# 术语表（名词与简写解释）

本文件集中解释项目文档中出现的**工程 / 流程 / 技术英文简写**，避免读文档时被缩写卡住。

> 分工：
> - **业务领域术语**（PersonUID、DataScope、采集源等）在 `docs/domain/established/ubiquitous-language.md`，本文件不重复。
> - **本文件**只收工程与流程类名词（方法论、分层、技术栈、目录约定）。

## 方法论与流程

| 简写 / 名词 | 全称 | 含义 |
| --- | --- | --- |
| DDD | Domain-Driven Design，领域驱动设计 | 以业务领域为核心的建模与设计方法；先统一语言、划上下文边界，再落代码 |
| 限界上下文 | Bounded Context | DDD 中一块有明确业务边界的领域范围，内部术语自洽，如 `identity-access` |
| 聚合根 | Aggregate Root | 一组强一致业务对象的唯一入口与一致性边界，如人员主档 `PersonRecord` |
| 统一语言 | Ubiquitous Language | 业务方与开发对同一概念使用同一套术语，落在 `ubiquitous-language.md` |
| OpenSpec | —（工具名） | 管理「变更提案 → 设计 → 任务 → 规格」生命周期的工作流工具，产物在 `docs/openspec/` |
| Superpowers | —（技能集名） | 一套 AI 研发方法论技能（测试驱动、调试、验证、评审等） |
| ADR | Architecture Decision Record，架构决策记录 | 一页纸记录「某个重要决策 + 当时为什么这么决定」，在 `docs/decisions/` |
| TDD | Test-Driven Development，测试驱动开发 | 先写失败测试，再写实现使其通过 |
| AC | Acceptance Criteria，验收标准 | 一条可验证的「满足什么才算做对」，通常写成 GIVEN/WHEN/THEN |
| GIVEN / WHEN / THEN | 给定 / 当 / 则 | 描述验收场景的固定句式：前置条件 / 触发操作 / 预期结果 |
| change | 变更（OpenSpec 中的一次提案单元） | 一次独立可归档的改动；本项目约定 = 一条端到端薄切片，不是整个模块 |
| capability | 能力（OpenSpec 规格单元） | 一块可独立描述的系统能力，对应 `docs/openspec/specs/<capability>/` |
| spike | 探针 / 探路 | 链路不清时做的**一次性、可丢弃**实验，只为摸通链路、把未知变成可回答的问题；代码不当成果交付 |
| In Scope / Out of Scope / Deferred | 范围内 / 范围外 / 暂缓 | 提案三段：本切片要做的 / 明确不做的 / 想不清留给后续切片的 |
| delta specs | 增量规格 | 一个 change 相对主规格的差异部分，归档时 `sync` 合并进主规格 |
| Plan Mode | 计划模式 | 先出方案、人审、再执行的轻量流程，用于 🟢/🟡 档需求 |
| Approval Gate | 审批闸门 | 命中即必须暂停请人工确认的高风险操作（删表、加关键依赖、权限变更等） |
| kebab-case | 短横线命名 | 全小写、单词用 `-` 连接，如 `identity-basic-ingest` |
| frontmatter | 文档头信息 | Markdown 文件顶部用 `---` 包裹的元数据（如 `status: inbox`） |
| worktree | Git 工作树 | 同一仓库挂出的独立工作目录，用于隔离不同分支的开发 |

## 角色分层与后端约定

| 简写 / 名词 | 全称 | 含义 |
| --- | --- | --- |
| Controller | 控制器 | 只做请求编排与参数校验，不放核心业务 |
| Service | 服务层 | 承载业务逻辑 |
| Mapper | 数据访问接口（MyBatis-Plus） | 操作数据库的接口，作为 DB 依赖边界 |
| BaseMapper | MyBatis-Plus 基础 Mapper | 提供通用增删改查的父接口 |
| DTO | Data Transfer Object，数据传输对象 | 接口出入参数据结构 |
| VO | View Object，视图对象 | 返给前端展示用的数据结构 |
| infrastructure / application / domain / interfaces | 基础设施 / 应用 / 领域 / 接口层 | 复杂需求用的四层 DDD 分层；简单需求裁剪为三层 Controller→Service→Mapper |
| CRUD | Create/Read/Update/Delete | 增、查、改、删四类基础操作 |

## 技术栈

| 简写 / 名词 | 全称 | 含义 |
| --- | --- | --- |
| Spring Boot | —（框架名） | Java 后端主框架 |
| MyBatis-Plus | —（框架名） | MyBatis 增强的持久层框架 |
| Maven | —（构建工具） | Java 项目构建与依赖管理工具 |
| Reactor | Maven 多模块反应堆 | Maven 同时构建多个子模块的机制（父 POM + 子模块） |
| POM | Project Object Model（`pom.xml`） | Maven 的项目与依赖配置文件 |
| Flyway | —（工具名） | 数据库迁移工具，用版本化 SQL 脚本管理表结构（结构真相源） |
| MySQL / H2 | —（数据库） | MySQL 为开发/生产库；H2 为测试用内存库 |
| OpenAPI | —（接口规范） | 描述 REST 接口契约的标准；后端经 springdoc 暴露 |
| springdoc-openapi | —（库名） | 由代码自动生成 OpenAPI 文档的库 |
| Actuator | Spring Boot Actuator | 提供健康检查、运行指标等运维端点 |
| JWT | JSON Web Token | 一种自包含的登录令牌格式 |
| BCrypt | —（算法名） | 密码哈希算法，禁止明文存储密码 |
| Feign / RestTemplate | —（HTTP 客户端） | 调用外部服务的 HTTP 客户端，属第三方依赖边界 |
| CORS | Cross-Origin Resource Sharing，跨域资源共享 | 浏览器跨域访问后端的放行配置 |
| profile | Spring 环境档 | 区分 `dev` / `test` 等不同环境的配置集 |
| Vue3 | —（前端框架） | 前端主框架 |
| Vite | —（构建工具） | 前端开发与打包工具 |
| Vitest | —（测试框架） | 配合 Vite 的前端单元测试框架 |
| Element Plus | —（组件库） | Vue3 的 UI 组件库 |
| moduleKey | 模块键 | 前端模块的唯一标识，连接菜单、路由与后端权限点 |
| e2e | end-to-end，端到端测试 | 模拟真实用户全链路的测试 |
| snapshot | 快照测试 | 比对 UI/输出与既存快照是否一致的测试 |

## 数据与协作

| 简写 / 名词 | 全称 | 含义 |
| --- | --- | --- |
| RBAC | Role-Based Access Control，基于角色的访问控制 | 用户—角色—权限的多对多授权模型 |
| ER 图 | Entity-Relationship Diagram，实体关系图 | 表达表/实体之间业务关系的图（本项目只画关系，不复制字段 DDL） |
| DDL | Data Definition Language | 建表/改表的 SQL 语句；结构真相以 Flyway 脚本为准 |
| ETL | Extract-Transform-Load，抽取-转换-加载 | 从数据源采集、清洗、入库的数据处理流程 |
| CI | Continuous Integration，持续集成 | 自动构建与测试的流水线（本项目暂未引入） |
| MCP | Model Context Protocol | 让 AI 调用外部工具/数据的协议；本项目用 MySQL MCP 只读读取库结构 |
| Mock / MockBean | 模拟 / 模拟 Bean | 测试中用假实现替代真实依赖（如外部 Feign） |
| Demo | 演示原型 | 早期 HTML 静态原型，位于演示目录，仅作参考 |

## 项目目录与状态术语

| 名词 | 含义 |
| --- | --- |
| context-map | 上下文映射：限界上下文之间的关系图，在 `docs/domain/*/context-map.md` |
| domain-model | 领域模型：聚合、实体、值对象等结构 |
| data-dictionary | 数据字典：表字段的业务语义、状态机、约束理由（DDL 之外的知识） |
| capability-map | 能力追溯索引：模块 → 前端入口 → 后端能力 → 相关表 → Demo 的薄指针 |
| developing / established | 开发中（可能回滚）/ 既定事实；领域文档的两个阶段 |
| inbox / shipped / dropped | 需求的三种状态目录：待办进行中 / 已完成 / 已放弃 |
