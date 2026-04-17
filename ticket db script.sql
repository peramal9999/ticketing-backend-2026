CREATE DATABASE ticketing_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LOCALE_PROVIDER = 'libc'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- for search later

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  first_name TEXT NOT NULL,
  last_name TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,

  role TEXT NOT NULL CHECK (
    role IN ('ADMIN', 'SUPPORT', 'END_USER')
  ),

  is_active BOOLEAN DEFAULT TRUE,
  last_login TIMESTAMP,

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE clients (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  contact_email TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE client_users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

  UNIQUE (client_id, user_id)
);

CREATE TABLE projects (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,

  name TEXT NOT NULL,
  description TEXT,
  short_code TEXT UNIQUE NOT NULL,

  is_active BOOLEAN DEFAULT FALSE,

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT UNIQUE NOT NULL,
  description TEXT
);


CREATE TABLE tickets (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  ticket_number TEXT UNIQUE NOT NULL,

  title TEXT NOT NULL,
  description TEXT,

  client_id UUID REFERENCES clients(id),
  project_id UUID REFERENCES projects(id),
  category_id UUID REFERENCES categories(id),

  priority TEXT DEFAULT 'MEDIUM'
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

  status TEXT DEFAULT 'OPEN'
    CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),

  created_by UUID NOT NULL REFERENCES users(id),
  assigned_to UUID REFERENCES users(id),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id),

  message TEXT NOT NULL,

  is_internal BOOLEAN DEFAULT FALSE,

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE attachments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
  comment_id UUID REFERENCES comments(id) ON DELETE CASCADE,

  file_name TEXT NOT NULL,
  file_url TEXT NOT NULL,
  file_type TEXT,
  file_size INT,

  uploaded_by UUID REFERENCES users(id),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ticket_status_history (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,

  old_status TEXT,
  new_status TEXT,

  changed_by UUID REFERENCES users(id),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    '$2a$10$3PJJiqP/DQkpHZXfss5e6OdjZ6E6wWIbOOeMzTd/asdsLCaChKYvK',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

INSERT INTO categories (name, description) VALUES
('IT_SUPPORT', 'General IT support issues'),
('HR_REQUEST', 'General HR queries'),
('ADMIN_REQUEST', 'General admin requests'),
('REPOSITORY_ACCESS', 'Permission or role changes');

-- ─────────────────────────────────────────────────────────────────────────────
-- Migration: add short_code to projects (run against existing databases only)
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE projects ADD COLUMN IF NOT EXISTS short_code TEXT;
ALTER TABLE projects ADD CONSTRAINT projects_short_code_unique UNIQUE (short_code);