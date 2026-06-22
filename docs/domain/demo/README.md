# Demo DDD 领域梳理（分析稿）

> 来源：`docs/原始demo/页面主页.html` 及同目录 12 个 HTML 原型  
> 对齐：`docs/domain/established/*`（已归档领域口径）  
> 状态：**分析稿**，未写入 `developing/` 或 `established/`；归档 OpenSpec change 前仅供评审

## 文档索引

| 文件 | 内容 |
| --- | --- |
| [ubiquitous-language.md](./ubiquitous-language.md) | 统一语言：核心名词、动词、Demo 页面追溯 |
| [context-map.md](./context-map.md) | 战略设计：限界上下文、域类型、Context Map |
| [domain-model.md](./domain-model.md) | 战术设计：聚合根、实体、值对象、领域事件、不变量 |
| [use-cases.md](./use-cases.md) | 核心用例：命令→应用服务→聚合→事件流转 |
| [demo-ddd-analysis.md](./demo-ddd-analysis.md) | 上述内容的合并版（便于一次性阅读） |

## 已确认建模假设

- 平台核心域是**人员身份主数据治理**，不是页面菜单或数据库表集合。
- **身份权限管理**本期以对账治理、差异识别、处置推送为边界；授权申请/审批为后续扩展。
- **校友**可通过独立表/接口接入，不默认等同「在档有效身份人员」；是否纳入活跃统计需显式业务状态。
- 跨上下文引用使用稳定 ID：`PersonUID`、`OrgNode.code`、`PermissionItem.code`。

## Demo 页面 → 限界上下文（速查）

| Demo 页面 | 主要上下文 |
| --- | --- |
| `页面主页.html` | platform-shell（导航/KPI 看板，聚合读模型） |
| `m1-basic-identity.html` | identity-master + data-ingestion |
| `m2-classification.html` / `m2-classification-admin.html` | identity-dimension（分类） |
| `m3-position.html` | identity-dimension（岗位）+ org-structure |
| `m4-org.html` | org-structure |
| `m7-custom-tags.html` | identity-dimension（自定义标签） |
| `m5-identity-permission.html` | permission-reconciliation |
| `m6-data-query.html` | data-query |
| `etl-monitor.html` / `source-maintenance.html` | data-ingestion |
| （全站顶栏登录用户） | identity-access |
