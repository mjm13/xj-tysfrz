# 能力追溯索引（Capability Map）

本文件是「业务能力追溯索引」，用于快速定位菜单模块对应的前端入口、后端能力、数据语义与 Demo 原型。

## 使用边界

- 本文件只记录模块级指针，不复制子菜单、路由明细或表结构。
- 前端路由真相在 `frontend/src/router/index.ts`，侧栏菜单真相在 `frontend/src/config/module-nav.ts`。
- 数据库结构真相在 Flyway 迁移脚本，表字段业务语义在 `docs/domain/established/data-dictionary.md`。
- OpenSpec 主规格真相在 `docs/openspec/specs/<capability>/spec.md`。
- `platform-shell` 属于前端平台壳层 / 架构能力，放在本索引与 `docs/architecture.md` 中追溯，不写入 DDD context map。

## 模块追溯索引

| 模块 | moduleKey | 前端入口 | 后端能力 / 领域上下文 | 相关数据表 | Demo 原型 | 当前状态 |
| --- | --- | --- | --- | --- | --- | --- |
| 平台首页 | `home` | `/` | `platform-shell` | 暂无业务表 | `页面主页.html` / `platform-v2-home.html` | 已接入壳层 |
| 平台管理 | `platform-admin` | `/admin/users` | `identity-access`（平台用户 CRUD UI + org_node 只读） | `platform_user` / `org_node` | 无 | 切片 1：平台用户列表/创建已接入 |
| 人员基础身份 | `identity-basic` | `/identity/basic` | `identity-master` + `data-ingestion` | 未落表 | `m1-basic-identity.html` | 前端占位，领域已建模 |
| 人员分类身份 | `identity-classification` | `/identity/classification` | `identity-dimension` | 未落表 | `m2-classification.html` / `m2-classification-admin.html` | 前端占位，领域已建模 |
| 人员岗位身份 | `identity-position` | `/identity/position` | `identity-dimension` + `org-structure` | 未落表 | `m3-position.html` | 前端占位，领域已建模 |
| 组织机构体系 | `identity-org` | `/identity/org` | `org-structure` | `org_node` | `m4-org.html` | 最小组织种子已落地 |
| 自定义标签身份 | `identity-tags` | `/identity/tags` | `identity-dimension` | 未落表 | `m7-custom-tags.html` | 前端占位，领域已建模 |
| 身份权限管理 | `identity-permission` | `/identity/permission` | `permission-reconciliation` + `identity-access` | `platform_user` / `platform_role` / `platform_permission` / `platform_user_role` / `platform_role_permission` | `m5-identity-permission.html` | 登录、RBAC、数据范围已落地；权限对账业务未落表 |
| 数据查询 | `services-query` | `/services/query` | `data-query` | 未落表 | `m6-data-query.html` | 前端占位，领域已建模 |
| ETL 监控 | `services-etl` | `/services/etl` | `data-ingestion` | 未落表 | `etl-monitor.html` | 前端占位，领域已建模 |
| 源头维护 | `services-sources` | `/services/sources` | `data-ingestion` | 未落表 | `source-maintenance.html` | 前端占位，领域已建模 |

## 维护规则

- 新增或下线模块时更新本索引。
- 子菜单、页面标题、字段类型等实现细节不写入本文件，避免与代码或 Flyway 漂移。
- 若模块新增业务表，只在「相关数据表」列补表名；字段语义写入数据字典。
