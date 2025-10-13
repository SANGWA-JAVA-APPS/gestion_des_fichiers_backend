-- Auto-executed by Spring Boot on startup
-- This will populate the docstatus table if empty

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Applicable' as name, 'Document is applicable and can be used' as description, 1 as active, NOW() as created_at, NOW() as updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Applicable');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Suspended', 'Document is temporarily suspended', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Suspended');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Replaced', 'Document has been replaced by a newer version', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Replaced');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Canceled', 'Document has been canceled', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Canceled');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'In Progress', 'Document is currently in progress', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'In Progress');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Valid', 'Document is valid and active', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Valid');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Rejected', 'Document has been rejected', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Rejected');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Rental', 'Document is related to rental', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Rental');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Solid', 'Document is solid/permanent', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Solid');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Free', 'Document is free/available', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Free');

INSERT INTO docstatus (name, description, active, created_at, updated_at) 
SELECT * FROM (SELECT 'Expired', 'Document has expired', 1, NOW(), NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM docstatus WHERE name = 'Expired');
