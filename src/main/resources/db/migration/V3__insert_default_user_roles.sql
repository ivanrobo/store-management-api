-- Insert default roles
INSERT INTO role (name, description) VALUES 
    ('USER', 'Standard user with basic permissions'),
    ('MANAGER', 'Manager with product management permissions'),
    ('ADMIN', 'Administrator with full system access')
ON CONFLICT (name) DO NOTHING;

-- Insert test users (passwords are BCrypt encoded)
-- admin123 -> $2a$10$8K1p/2T7UEV6Zk8Ux7ZHmuBzJr2jmfD8E5Q2F7x8Q1j4Y3pR0Xm6G
-- manager123 -> $2a$10$9L2q/3U8VFW7Al9Vy8AInu4CzKs3knfE9F6R3G8yR2k5Z4qS1Yn7H
INSERT INTO users (username, password, email, enabled) VALUES 
    ('admin', '$2a$10$8K1p/2T7UEV6Zk8Ux7ZHmuBzJr2jmfD8E5Q2F7x8Q1j4Y3pR0Xm6G', 'admin@test.com', true),
    ('manager', '$2a$10$9L2q/3U8VFW7Al9Vy8AInu4CzKs3knfE9F6R3G8yR2k5Z4qS1Yn7H', 'manager@test.com', true)
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
