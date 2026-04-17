-- =============================================================================
-- Default Users Seed Script
-- Run this AFTER executing "ticket db script.sql"
--
-- INSTRUCTIONS:
--   1. Generate a BCrypt hash for your desired password using any tool:
--      - Online : https://bcrypt-generator.com  (rounds = 10)
--      - CLI    : htpasswd -bnBC 10 "" YourPassword | tr -d ':\n'
--   2. Replace the placeholder  <<BCRYPT_HASH_HERE>>  with your hash.
--   3. Run this script against the ticketing_db database.
-- =============================================================================

-- ADMIN user
INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    password_hash,
    role,
    is_active,
    created_at,
    updated_at
)
VALUES (
    uuid_generate_v4(),
    'System',
    'Admin',
    'admin@ticketingsys.com',
    '<<BCRYPT_HASH_HERE>>',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- =============================================================================
-- Verify
-- =============================================================================
SELECT id, first_name, last_name, email, role, is_active, created_at
FROM users
WHERE email = 'admin@ticketingsys.com';
