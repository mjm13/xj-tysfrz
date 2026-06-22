## 1. 对齐

- [x] 1.1 阅读 `009-platform-admin-departments.md` 与 proposal
- [x] 1.2 确认无 delete API、无 OrgMapping

## 2. Flyway V5

- [x] 2.1 V5 admin:departments:read/write（prod + migration-test）
- [x] 2.2 授予 ADMIN；SystemInfoControllerTest flyway 版本更新

## 3. 后端 — OrgNode Admin API

- [x] 3.1 DTO：CreateOrgNodeRequest、UpdateOrgNodeRequest
- [x] 3.2 扩展 OrgNodeAdminAppService：roots、children、create、update、cycle 检测
- [x] 3.3 扩展 OrgNodeAdminController：`/roots`、`/children`、POST、PUT
- [x] 3.4 `OrgNodeAdminIntegrationTest` 全绿

## 4. 后端 — 验证

- [x] 4.1 `backend/mvnw -pl xj-tysfrz-business -am test` 全绿

## 5. 前端

- [x] 5.1 `permissions.ts`：`/admin/departments` + PLATFORM_ADMIN 权限
- [x] 5.2 router、module-nav、AppTopbar
- [x] 5.3 `api/admin.ts` org tree API
- [x] 5.4 `AdminDepartmentsView.vue`（ElTree lazy）
- [x] 5.5 tests + build

## 6. 文档与收尾

- [x] 6.1 capability-map、009 AC、验收记录
- [x] 6.2 sync → archive → sync-knowledge
- [x] 6.3 **git commit**（需求最终操作）

## 7. 不在本 change

- org 节点删除、OrgMapping、identity-org UI
