# Domain Docs Index

`docs/domain` 按“既定事实/开发中”物理隔离：

- `established/`：已归档、可作为事实引用的领域文档
- `developing/`：开发中、可能回滚的领域文档

## 使用约定

- 业务/混合 change 在实现前后的领域建模，写入 `developing/`
- 完成 `sync -> archive -> sync-knowledge` 后，再将有效内容提升到 `established/`
- 放弃 change 时，回滚 `developing/` 中对应条目

## 文件清单

- `established/context-map.md`
- `established/ubiquitous-language.md`
- `established/domain-model.md`
- `established/data-dictionary.md`
- `developing/context-map.md`
- `developing/ubiquitous-language.md`
- `developing/domain-model.md`
- `developing/data-dictionary.md`
- `demo/`：基于原始 Demo 的 DDD 分析稿（未归档，见 [demo/README.md](../demo/README.md)）

## 沉淀边界（重要）

- 沉淀「代码外知识」：业务规则、术语、决策原因、数据库业务语义
- **不沉淀代码**：代码即文档，抄进来必然漂移
- 数据库结构真相在 Flyway，本目录只记录业务语义（数据字典）
- 沉淀与流程档位无关：🟢 轻量需求命中业务规则/数据语义也要沉淀（详见 `.cursor/rules/00-workflow.mdc` 知识沉淀章节）
