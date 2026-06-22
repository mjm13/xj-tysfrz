# 03 — Demo 与现状差距分析

## 1. 架构层差距

| 维度 | Demo | 现状 | 差距 |
| --- | --- | --- | --- |
| 导航模型 | 顶栏 + 模块 Sidebar 双层 | 仅顶栏 | 缺 `ModuleLayout` 与模块路由嵌套 |
| 路由数量 | 12 HTML 页面互链 | 2 路由 | 缺 10+ 业务路由 |
| 数据层 | 内联 JS + sysu-*.js | 首页少量硬编码 | 缺 mock 模块、缺 API 契约 |
| 鉴权 | 无（假定已登录） | Mock 前端鉴权 | 真实 identity-access 未启动 |
| 文档真相 | demo HTML | OpenSpec 仍写侧栏/指标平台 | **spec/domain 漂移** |

## 2. 壳层差距（Phase 0）

| Demo 能力 | 现状 | 差距说明 |
| --- | --- | --- |
| 顶栏 Logo + 文案 | ✅ | — |
| 顶栏「主页」高亮 | ✅ 仅 `/` | — |
| 身份管理下拉 5 项 | ⚠️ UI 有，链接无效 | 需 RouterLink + 路由 |
| 身份权限 / 数据查询顶栏项 | ⚠️ `#` 占位 | 需路由 `/identity-permission`、`/data-query` |
| 实时时钟 | ✅ | — |
| 用户信息「管理员 · 数据治理岗」 | ⚠️ 用户名来自 Mock，岗位写死 | 后续接真实用户 Profile |
| 页脚 | ✅ | — |
| 模块 Sidebar（人员管理 5 项互链） | ❌ | 所有 m1~m7、m4 页面共有模式 |
| 面包屑组件 | ⚠️ 仅 HomeView 内联 | 需提取复用 |
| page-head（标题+描述+统计） | ⚠️ 仅 HomeView 内联 | 需提取复用 |
| 共用 filter-bar / data-card / tree | ❌ | Demo 各页重复 CSS，需组件化 |

## 3. 首页差距（`/`）

| Demo 区块 | 现状 | 差距 |
| --- | --- | --- |
| 面包屑「平台总览」 | ✅ | — |
| 页面标题 + 3 项统计 | ✅ Mock | 数据应接 dashboard API |
| 7 功能导航卡 | ✅ UI | 点击仅 `ElMessage`，应路由跳转 |
| 人员总览卡 + 类型列表 | ✅ Mock | 接统计 API |
| 权限饼图 + 图例联动 | ✅ Mock | 接统计 API |
| 4 权限分布卡 | ✅ Mock | 接统计 API |

## 4. 业务页差距（按 Demo 文件）

| Demo | 建议路由前缀 | 视图数 | 现状 | 核心缺口 |
| --- | --- | --- | --- | --- |
| m1 人员基础身份 | `/identity/basic` | 2+ | ❌ | 人员列表/筛选/详情抽屉、变更记录、ETL 入口 |
| m2 人员分类身份 | `/identity/classification` | 4 | ❌ | 分类树、映射/待映射/变更、导入导出 |
| m2-admin 分类管理 | `/identity/classification/admin` | 4 | ❌ | 树 CRUD、映射规则、花名册 |
| m3 人员岗位身份 | `/identity/position` | 2 | ❌ | 只读岗位表、来源映射 |
| m4 组织机构 | `/identity/org` | 3 | ❌ | 组织树编辑、来源映射、变更；依赖 sysu-org |
| m7 自定义标签 | `/identity/tags` | 2 | ❌ | 标签组 CRUD、组内人员 |
| m5 身份权限管理 | `/identity/permission` | 8 | ❌ | 矩阵/对账/僵尸/规则/任务等治理视图 |
| m6 数据查询 | `/services/query` | 3 | ❌ | 主题表查询、SQL 工作台 |
| etl-monitor | `/services/etl` | 1 | ❌ | 任务监控 |
| source-maintenance | `/services/sources` | 2 | ❌ | 源头维护 |

## 5. 后端差距

| Demo 隐含能力 | 现状 | 说明 |
| --- | --- | --- |
| 人员主档 CRUD / 查询 | ❌ | m1 |
| 分类树 / 映射 | ❌ | m2 + sysu-cls 数据结构 |
| 组织树 | ❌ | m4 + sysu-org |
| 岗位 / 标签 | ❌ | m3、m7 |
| 权限项 / 授权矩阵 / 对账 | ❌ | m5，领域复杂度高 |
| 即席查询 / 主题表 | ❌ | m6 |
| ETL 任务 / 数据源 | ❌ | etl、source-maintenance |
| Dashboard 汇总统计 | ❌ | 首页 |

## 6. 知识沉淀差距

| 应沉淀内容 | 现状 |
| --- | --- |
| 限界上下文：basic-identity、classification、org、position、tags、permission、data-query、system-services | ❌ 未建模 |
| 统一语言：在档人员、分类节点、来源映射、权限项、对账… | ❌ |
| 数据字典：人员/分类/组织/权限表 | ❌ Flyway 无表 |
| platform-shell spec 更新（顶栏 + ModuleLayout） | ❌ 仍描述侧栏 |

## 7. 文档漂移（需单独任务处理）

以下已交付文档与当前 Demo/代码**不一致**，后续 change 应一并修正：

1. `openspec/specs/platform-shell/spec.md` — 侧栏 → 顶栏 + 模块布局  
2. `docs/domain/established/context-map.md` — platform-shell 描述  
3. `docs/domain/established/ubiquitous-language.md` — Main Layout 定义  
4. `docs/requirements/inbox/001-frontend-app-shell.md` — 参照 demo 名称/布局  

建议立项：`platform-shell-v2-identity-demo`（🟡 技术 + 文档同步）
