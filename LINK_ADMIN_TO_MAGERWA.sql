-- ========================================
-- Link Admin User to MAGERWA Company
-- ========================================
-- This script links the default admin user to MAGERWA company
-- and assigns all available permissions to the admin account

-- 1. Link admin to MAGERWA location entity
UPDATE accounts 
SET location_entity_id = 1 
WHERE username = 'admin';

-- 2. Assign all permissions to admin
INSERT INTO account_permissions (account_id, permission_id)
SELECT 1, p.id 
FROM permissions p
WHERE NOT EXISTS (
    SELECT 1 FROM account_permissions ap 
    WHERE ap.account_id = 1 AND ap.permission_id = p.id
);

-- 3. Verify the update
SELECT 
    a.id as account_id,
    a.username,
    a.full_name,
    a.location_entity_id,
    le.name as location_entity_name,
    c.name as country_name,
    c.iso_code as country_iso_code,
    COUNT(ap.permission_id) as total_permissions
FROM accounts a
LEFT JOIN location_entities le ON a.location_entity_id = le.id
LEFT JOIN countries c ON le.country_id = c.id
LEFT JOIN account_permissions ap ON a.id = ap.account_id
WHERE a.username = 'admin'
GROUP BY a.id, a.username, a.full_name, a.location_entity_id, le.name, c.name, c.iso_code;
