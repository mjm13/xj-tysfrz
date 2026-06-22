-- admin departments (org_node) management permissions (identity-access)
INSERT INTO platform_permission (id, permission_code, module_name, action_name) VALUES
(15, 'admin:departments:read', 'admin', 'departments-read'),
(16, 'admin:departments:write', 'admin', 'departments-write');

INSERT INTO platform_role_permission (role_id, permission_id) VALUES
(1, 15),
(1, 16);
