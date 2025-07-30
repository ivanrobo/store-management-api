-- Insert default roles
INSERT INTO role (name, description) VALUES 
    ('USER', 'Standard user with basic permissions'),
    ('MANAGER', 'Manager with product management permissions'),
    ('ADMIN', 'Administrator with full system access')
ON CONFLICT (name) DO NOTHING;

-- Insert test users (passwords are BCrypt encoded)
INSERT INTO users (username, password, email, enabled) VALUES 
    ('admin', '$2a$10$boE8bO8r0Ywa.jvlNozWX.HUVQGpxtb4KVVo8MYfPZzFgHbP4O/fG', 'admin@test.com', true),
    ('manager', '$2a$10$Ehet9AdpeFgZjfej3XrCIOD.QPS0uROj5xMXCqrsEid2ZmWt9i/Qa', 'manager@test.com', true)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to test users
INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, role r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, role r 
WHERE u.username = 'manager' AND r.name = 'MANAGER'
ON CONFLICT DO NOTHING;
