-- org_node 测试最小集：覆盖 DataScope / DepartmentRef 集成测试所需节点（H2 兼容语法）
CREATE TABLE IF NOT EXISTS org_node (
    code        VARCHAR(64)  NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    parent_code VARCHAR(64)  NULL,
    level       INT          NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_org_node_parent ON org_node (parent_code);

INSERT INTO org_node (code, name, parent_code, level) VALUES
  ('SYSU', '中山大学', NULL, 1),
  ('CAT-admin', '行政部门', 'SYSU', 2),
  ('CAT-party', '党群部门', 'SYSU', 2),
  ('CAT-research', '科研机构', 'SYSU', 2),
  ('CAT-teaching', '教学单位', 'SYSU', 2),
  ('01020', '党委办公室（党委信访办公室）', 'CAT-party', 3);
