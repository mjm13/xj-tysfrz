-- admin roles management permissions (test, mirrors prod V4)
INSERT INTO platform_permission (id, permission_code, module_name, action_name) VALUES
(13, 'admin:roles:read', 'admin', 'roles-read'),
(14, 'admin:roles:write', 'admin', 'roles-write');

INSERT INTO platform_role_permission (role_id, permission_id) VALUES
(1, 13),
(1, 14);
