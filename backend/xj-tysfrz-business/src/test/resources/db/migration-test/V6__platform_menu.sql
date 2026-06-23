-- platform navigation menus (H2 test)
CREATE TABLE IF NOT EXISTS platform_menu (
    menu_code   VARCHAR(128) NOT NULL PRIMARY KEY,
    label       VARCHAR(128) NOT NULL,
    path        VARCHAR(255) NULL,
    parent_code VARCHAR(128) NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    menu_type   VARCHAR(16)  NOT NULL,
    module_key  VARCHAR(64)  NULL,
    visible     BOOLEAN      NOT NULL DEFAULT TRUE,
    description VARCHAR(255) NULL
);

CREATE INDEX IF NOT EXISTS idx_platform_menu_parent ON platform_menu (parent_code);
CREATE INDEX IF NOT EXISTS idx_platform_menu_module ON platform_menu (module_key);

CREATE TABLE IF NOT EXISTS platform_menu_permission (
    menu_code     VARCHAR(128) NOT NULL,
    permission_id BIGINT       NOT NULL,
    PRIMARY KEY (menu_code, permission_id)
);

INSERT INTO platform_permission (id, permission_code, module_name, action_name) VALUES
(17, 'admin:menus:read', 'admin', 'menus-read'),
(18, 'admin:menus:write', 'admin', 'menus-write');

INSERT INTO platform_role_permission (role_id, permission_id) VALUES
(1, 17),
(1, 18);

INSERT INTO platform_menu (menu_code, label, path, parent_code, sort_order, menu_type, module_key, visible, description) VALUES
('nav.top.home', '主页', '/', NULL, 1, 'LINK', NULL, TRUE, NULL),
('nav.top.identity', '身份管理', NULL, NULL, 2, 'GROUP', NULL, TRUE, NULL),
('nav.top.identity.basic', '人员基础身份', '/identity/basic', 'nav.top.identity', 1, 'LINK', NULL, TRUE, '人员进档'),
('nav.top.identity.classification', '人员分类身份', '/identity/classification', 'nav.top.identity', 2, 'LINK', NULL, TRUE, '树形分类'),
('nav.top.identity.position', '人员岗位身份', '/identity/position', 'nav.top.identity', 3, 'LINK', NULL, TRUE, '广义岗位'),
('nav.top.identity.tags', '人员自定义标签管理', '/identity/tags', 'nav.top.identity', 4, 'LINK', NULL, TRUE, '自定义群组'),
('nav.top.identity.org', '组织机构体系', '/identity/org', 'nav.top.identity', 5, 'LINK', NULL, TRUE, '院系树'),
('nav.top.permission', '身份权限管理', '/identity/permission', NULL, 3, 'LINK', NULL, TRUE, NULL),
('nav.top.services', '数据查询', '/services/query/identity', NULL, 4, 'LINK', NULL, TRUE, NULL),
('nav.top.platform', '平台管理', NULL, NULL, 5, 'GROUP', NULL, TRUE, NULL),
('nav.top.platform.users', '平台用户', '/admin/users', 'nav.top.platform', 1, 'LINK', NULL, TRUE, '平台用户'),
('nav.top.platform.roles', '角色管理', '/admin/roles', 'nav.top.platform', 2, 'LINK', NULL, TRUE, '角色管理'),
('nav.top.platform.departments', '部门管理', '/admin/departments', 'nav.top.platform', 3, 'LINK', NULL, TRUE, '部门管理'),
('nav.top.platform.menus', '菜单管理', '/admin/menus', 'nav.top.platform', 4, 'LINK', NULL, TRUE, '菜单管理'),
('nav.sidebar.platform-admin.group', '平台管理', NULL, NULL, 1, 'GROUP', 'platform-admin', TRUE, NULL),
('nav.sidebar.platform-admin.users', '平台用户', '/admin/users', 'nav.sidebar.platform-admin.group', 1, 'LINK', 'platform-admin', TRUE, NULL),
('nav.sidebar.platform-admin.roles', '角色管理', '/admin/roles', 'nav.sidebar.platform-admin.group', 2, 'LINK', 'platform-admin', TRUE, NULL),
('nav.sidebar.platform-admin.departments', '部门管理', '/admin/departments', 'nav.sidebar.platform-admin.group', 3, 'LINK', 'platform-admin', TRUE, NULL),
('nav.sidebar.platform-admin.menus', '菜单管理', '/admin/menus', 'nav.sidebar.platform-admin.group', 4, 'LINK', 'platform-admin', TRUE, NULL);

INSERT INTO platform_menu_permission (menu_code, permission_id) VALUES
('nav.top.home', 1),
('nav.top.identity.basic', 2),
('nav.top.identity.classification', 3),
('nav.top.identity.position', 4),
('nav.top.identity.tags', 5),
('nav.top.identity.org', 6),
('nav.top.permission', 7),
('nav.top.services', 8),
('nav.top.platform.users', 11), ('nav.top.platform.users', 12),
('nav.top.platform.roles', 13), ('nav.top.platform.roles', 14),
('nav.top.platform.departments', 15), ('nav.top.platform.departments', 16),
('nav.top.platform.menus', 17), ('nav.top.platform.menus', 18),
('nav.sidebar.platform-admin.users', 11), ('nav.sidebar.platform-admin.users', 12),
('nav.sidebar.platform-admin.roles', 13), ('nav.sidebar.platform-admin.roles', 14),
('nav.sidebar.platform-admin.departments', 15), ('nav.sidebar.platform-admin.departments', 16),
('nav.sidebar.platform-admin.menus', 17), ('nav.sidebar.platform-admin.menus', 18);
