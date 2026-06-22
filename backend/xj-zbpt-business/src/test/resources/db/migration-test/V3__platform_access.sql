-- platform access: users, roles, permissions (identity-access)
-- 测试环境：结构与生产 V3 一致，种子用户供 AuthFlowIntegrationTest 等使用
CREATE TABLE platform_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role_code   VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(255) NULL,
    CONSTRAINT uk_platform_role_code UNIQUE (role_code)
);

CREATE TABLE platform_permission (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(128) NOT NULL,
    module_name     VARCHAR(64)  NOT NULL,
    action_name     VARCHAR(32)  NOT NULL,
    CONSTRAINT uk_platform_permission_code UNIQUE (permission_code)
);

CREATE TABLE platform_user (
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    platform_user_id VARCHAR(32)  NOT NULL,
    username         VARCHAR(64)  NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    status           VARCHAR(16)  NOT NULL,
    department_code  VARCHAR(64)  NOT NULL,
    data_scope       VARCHAR(32)  NOT NULL,
    CONSTRAINT uk_platform_user_id UNIQUE (platform_user_id),
    CONSTRAINT uk_platform_username UNIQUE (username)
);

CREATE TABLE platform_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE platform_role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO platform_role (id, role_code, name, description) VALUES
(1, 'ADMIN', '系统管理员', '全局管理员'),
(2, 'GOVERNANCE', '治理岗', '部门数据治理');

INSERT INTO platform_permission (id, permission_code, module_name, action_name) VALUES
(1,  'home:read', 'home', 'read'),
(2,  'identity-basic:read', 'identity-basic', 'read'),
(3,  'identity-classification:read', 'identity-classification', 'read'),
(4,  'identity-position:read', 'identity-position', 'read'),
(5,  'identity-tags:read', 'identity-tags', 'read'),
(6,  'identity-org:read', 'identity-org', 'read'),
(7,  'identity-permission:read', 'identity-permission', 'read'),
(8,  'services-query:read', 'services-query', 'read'),
(9,  'services-etl:read', 'services-etl', 'read'),
(10, 'services-sources:read', 'services-sources', 'read'),
(11, 'admin:users:read', 'admin', 'users-read'),
(12, 'admin:users:write', 'admin', 'users-write');

INSERT INTO platform_role_permission (role_id, permission_id)
SELECT 1, id FROM platform_permission;

INSERT INTO platform_role_permission (role_id, permission_id) VALUES
(2, 1), (2, 2), (2, 8);

-- admin / admin123 (BCrypt)
INSERT INTO platform_user (id, platform_user_id, username, password_hash, status, department_code, data_scope) VALUES
(1, 'PU0000000001', 'admin', '$2a$10$IZc3bhbs63qsp9dtzkZLVuQCRWGgqklyw.P4dzRAz10dzDyceYQkO', 'ACTIVE', 'SYSU', 'GLOBAL');

-- dept_admin / admin123 — OWN_DEPT_AND_SUB @ CAT-party (DataScope 测试)
INSERT INTO platform_user (id, platform_user_id, username, password_hash, status, department_code, data_scope) VALUES
(2, 'PU0000000002', 'dept_admin', '$2a$10$IZc3bhbs63qsp9dtzkZLVuQCRWGgqklyw.P4dzRAz10dzDyceYQkO', 'ACTIVE', 'CAT-party', 'OWN_DEPT_AND_SUB');

INSERT INTO platform_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);
