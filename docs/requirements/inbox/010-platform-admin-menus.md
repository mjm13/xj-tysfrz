---
title: 平台管理 — 菜单管理（DB 驱动导航，切片 4）
status: inbox
change: platform-admin-menus
owner: team
createdAt: 2026-06-22
tier: 🔴
changeType: 混合
dependsOn: shipped/007-platform-admin-users, shipped/008-platform-admin-roles
relatedDomain: docs/domain/established/（identity-access）
---

# 背景

当前顶栏与模块侧栏菜单写死在 `AppTopbar.vue`、`module-nav.ts`。用户已确认：**菜单管理完成后，前台改为从数据库加载**，以便运营/管理员配置模块入口而无需发版改代码。

# 业务目标

- 「平台管理 → 菜单管理」：维护菜单树（模块、路由、排序、可见性、关联 permission）
- 登录后前端从 API 拉取当前用户可见菜单，替代静态配置（保留 Design Token 与布局不变）

# 范围切分

## 本切片 In Scope（规划，未启动）

- 新表（如 `platform_menu`）+ Flyway；菜单与 permission 关联规则
- Admin CRUD API + `/admin/menus` 管理页
- 运行时 API：`GET /api/menus`（或 `/api/navigation`）按 permissions 过滤
- 前端：顶栏/侧栏改为消费 API；静态 `module-nav` 逐步退役

## Out of Scope

- 业务页面内容（仍占位页）
- 多租户/多应用菜单隔离

## Open Questions & Deferred

- 菜单节点类型：仅 link vs 分组 vs 外链？
- 是否与 `moduleKey` 继续 1:1 绑定？
- 缓存与版本：菜单变更后是否强制前端刷新？

# 验收标准（待实现时细化）

- [ ] GIVEN 管理员在库中隐藏某模块菜单 WHEN 普通用户登录 THEN 顶栏不显示该入口且 direct URL 仍被守卫拦截
- [ ] GIVEN 管理员调整菜单顺序 WHEN 用户刷新 THEN 导航顺序与库一致

# 备注

- 涉及 **Approval Gate**：新表 + 导航架构变更，propose 前需确认 schema
- 008 角色/权限就绪后，菜单项与 permission 绑定更清晰
