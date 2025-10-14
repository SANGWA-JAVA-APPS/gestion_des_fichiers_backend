-- =========================================================
-- Insert document status values into docstatus table
-- Database: gestiondesfichier_db
-- =========================================================

USE gestiondesfichier_db;

-- First, check what's already in the table
SELECT 'Current docstatus records:' as info;
SELECT id, name, description, active FROM docstatus;

-- Check if records already exist to avoid duplicates
SELECT 'Checking for existing records...' as info;
SELECT name FROM docstatus WHERE name IN ('Applicable', 'Suspended', 'Replaced', 'Canceled', 'In Progress', 'Valid', 'Rejected', 'Rental', 'Solid', 'Free', 'Expired');

-- Insert the new status values (only if they don't exist)
SELECT 'Inserting new document status values...' as info;

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

-- Verify the inserted data
SELECT 'All docstatus records after insertion:' as info;
SELECT id, name, description, active, created_at FROM docstatus ORDER BY id;

-- Show count
SELECT 'Total document statuses:' as info, COUNT(*) as total_count FROM docstatus;
