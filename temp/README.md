# Demo → 项目 差距梳理（工作清单入口）

> 生成日期：2026-06-19  
> 参照 Demo：`docs/原始demo/页面主页.html` 及同目录静态原型全集

本目录用于**后续创建 OpenSpec change / requirements inbox 任务**前的差距梳理，非正式需求文档。

## 文档索引

| 文件 | 内容 |
| --- | --- |
| [01-demo-inventory.md](./01-demo-inventory.md) | 原始 Demo 信息架构、页面清单、共用组件与 Mock 数据 |
| [02-current-state.md](./02-current-state.md) | 当前仓库前端/后端/领域/OpenSpec 已交付状态 |
| [03-gap-analysis.md](./03-gap-analysis.md) | Demo 与现状逐项差距对照 |
| [04-work-backlog.md](./04-work-backlog.md) | **Phase 级路线图**（P0~P4，含后端/联调/质量） |
| [05-demo-feature-tasks.md](./05-demo-feature-tasks.md) | **页面级功能任务清单**（04 Phase 1 的细粒度展开，逐页 T-*，可直接立项） |
| [06-open-questions.md](./06-open-questions.md) | **逻辑疑问与断点台账**（逐页精读产出，🔴 项开发前必确认；被 05 引用） |
| [07-feature-map.md](./07-feature-map.md) | **功能模块与功能点清单**（产品功能视图：9 模块 + 功能点 + 成熟度标注） |

## 阅读顺序

1. 先读 `01` + `02` 建立全局视图  
2. 读 `03` 理解「还差什么」  
3. 从 `04` 按 Phase 挑选任务立项（建议从 Phase 0 → Phase 1 顺序）  
4. 立项具体页面时，配合 `05`（功能任务）+ `06`（先确认 🔴 疑问）

## 关键结论（摘要）

- Demo 是**顶栏 + 模块内左侧栏**的双层导航；当前 Vue 仅完成**顶栏 + 首页 Mock**，无模块内布局与业务路由。
- Demo 共 **12 个 HTML 页面 + 2 个 JS Mock 数据集**；当前仅 **`/` 与 `/login`** 两条路由。
- 后端仍为**技术骨架**（Ping + 空 Flyway），无身份业务 API。
- 已归档 OpenSpec `frontend-app-shell` 描述的是**旧「指标平台 + 侧栏」**壳层，与当前 Demo 方向存在**文档漂移**，后续 change 需同步更新 `platform-shell` spec 与 `docs/domain/established`。
