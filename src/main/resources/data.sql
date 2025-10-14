INSERT INTO docstatus (name, description, active, created_at, updated_at) VALUES
('Applicable', 'Document is applicable and can be used', 1, NOW(), NOW()),
('Suspended', 'Document is temporarily suspended', 1, NOW(), NOW()),
('Replaced', 'Document has been replaced by a newer version', 1, NOW(), NOW()),
('Canceled', 'Document has been canceled', 1, NOW(), NOW()),
('In Progress', 'Document is currently in progress', 1, NOW(), NOW()),
('Valid', 'Document is valid and active', 1, NOW(), NOW()),
('Rejected', 'Document has been rejected', 1, NOW(), NOW()),
('Rental', 'Document is related to rental', 1, NOW(), NOW()),
('Solid', 'Document is solid/permanent', 1, NOW(), NOW()),
('Free', 'Document is free/available', 1, NOW(), NOW()),
('Expired', 'Document has expired', 1, NOW(), NOW());