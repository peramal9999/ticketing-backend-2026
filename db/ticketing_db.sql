--
-- PostgreSQL database dump
--

\restrict aENeXlG9xLnuFOvWsbDHFQ00umlPJFgf83eQP5zO7htIupkkv27SN0szhj30QbZ

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: attachments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.attachments (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    ticket_id uuid NOT NULL,
    comment_id uuid,
    file_name text NOT NULL,
    file_url text NOT NULL,
    file_type text,
    file_size integer,
    uploaded_by uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.attachments OWNER TO postgres;

--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: client_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.client_users (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    client_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.client_users OWNER TO postgres;

--
-- Name: clients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clients (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name text NOT NULL,
    contact_email text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.clients OWNER TO postgres;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comments (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    ticket_id uuid NOT NULL,
    user_id uuid NOT NULL,
    message text NOT NULL,
    is_internal boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.comments OWNER TO postgres;

--
-- Name: projects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.projects (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    client_id uuid NOT NULL,
    name text NOT NULL,
    description text,
    is_active boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    short_code text NOT NULL
);


ALTER TABLE public.projects OWNER TO postgres;

--
-- Name: ticket_status_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ticket_status_history (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    ticket_id uuid NOT NULL,
    old_status text,
    new_status text,
    changed_by uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.ticket_status_history OWNER TO postgres;

--
-- Name: tickets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tickets (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    ticket_number text NOT NULL,
    title text NOT NULL,
    description text,
    client_id uuid,
    project_id uuid,
    category_id uuid,
    priority text DEFAULT 'MEDIUM'::text,
    status text DEFAULT 'OPEN'::text,
    created_by uuid NOT NULL,
    assigned_to uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tickets_priority_check CHECK ((priority = ANY (ARRAY['LOW'::text, 'MEDIUM'::text, 'HIGH'::text]))),
    CONSTRAINT tickets_status_check CHECK ((status = ANY (ARRAY['OPEN'::text, 'IN_PROGRESS'::text, 'RESOLVED'::text, 'CLOSED'::text])))
);


ALTER TABLE public.tickets OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text NOT NULL,
    password_hash text NOT NULL,
    role text NOT NULL,
    is_active boolean DEFAULT true,
    last_login timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_role_check CHECK ((role = ANY (ARRAY['ADMIN'::text, 'SUPPORT'::text, 'END_USER'::text])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: attachments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.attachments (id, ticket_id, comment_id, file_name, file_url, file_type, file_size, uploaded_by, created_at) FROM stdin;
a93bb231-72dc-4f83-9628-6e07337494c9	aba6b9da-79bf-4168-888f-288914440044	\N	What is a coding assistant.docx	tickets/aba6b9da-79bf-4168-888f-288914440044/896271ff-8b22-47ea-8212-881eff19d4c5.docx	application/vnd.openxmlformats-officedocument.wordprocessingml.document	834615	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	2026-04-10 20:42:00.088596
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categories (id, name, description) FROM stdin;
ca017e03-61e6-44ab-aa6a-7829996448f9	IT_SUPPORT	General IT support issues
674b6b2c-60bf-4c7f-9e63-2c1bdc582dde	HR_REQUEST	General HR queries
a250baea-d3db-4ecd-bfe5-fde3b4265c46	ADMIN_REQUEST	General admin requests
d0441bbf-a62d-4370-9ac5-3e9cd20023cc	REPOSITORY_ACCESS	Permission or role changes
\.


--
-- Data for Name: client_users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.client_users (id, client_id, user_id) FROM stdin;
\.


--
-- Data for Name: clients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.clients (id, name, contact_email, created_at) FROM stdin;
a89aaa73-27e7-4414-b19c-5a85803ae08e	Radian	kevin@yopmail.com	2026-04-10 20:18:40.567672
\.


--
-- Data for Name: comments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.comments (id, ticket_id, user_id, message, is_internal, created_at) FROM stdin;
\.


--
-- Data for Name: projects; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.projects (id, client_id, name, description, is_active, created_at, short_code) FROM stdin;
dd959636-dd9d-4b76-8fce-0ae1f81481f0	a89aaa73-27e7-4414-b19c-5a85803ae08e	Reporting	Reporting Tool	t	2026-04-10 20:19:52.551464	RADIAN
\.


--
-- Data for Name: ticket_status_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ticket_status_history (id, ticket_id, old_status, new_status, changed_by, created_at) FROM stdin;
6db4b670-b2e5-4865-86be-d485586bb508	aba6b9da-79bf-4168-888f-288914440044	\N	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	2026-04-10 20:22:26.631083
1d14a813-d166-4df1-ac24-50291f171abb	334221d4-b8de-44fc-ae7c-a1466cd3c9ac	\N	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	2026-04-10 22:25:20.909063
175ac15a-a508-4ab0-8fae-46f4ed590d24	12140d62-9478-4f5e-95e7-d84704463fff	\N	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	2026-04-10 22:25:26.766467
8c2bec43-8737-4021-82d9-48662cc3f0fe	dc905c84-dae5-4783-bfbf-1bfea2ae3509	\N	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	2026-04-10 22:25:36.941898
\.


--
-- Data for Name: tickets; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tickets (id, ticket_number, title, description, client_id, project_id, category_id, priority, status, created_by, assigned_to, created_at, updated_at) FROM stdin;
aba6b9da-79bf-4168-888f-288914440044	TKT-20260410-00001	Visits not showing 657343534-US	TEST	a89aaa73-27e7-4414-b19c-5a85803ae08e	dd959636-dd9d-4b76-8fce-0ae1f81481f0	\N	LOW	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	\N	2026-04-10 20:22:26.627705	2026-04-10 20:22:26.627705
334221d4-b8de-44fc-ae7c-a1466cd3c9ac	RADIAN-00001	App crash	App crash	a89aaa73-27e7-4414-b19c-5a85803ae08e	dd959636-dd9d-4b76-8fce-0ae1f81481f0	\N	LOW	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	\N	2026-04-10 22:25:20.886186	2026-04-10 22:25:20.886186
12140d62-9478-4f5e-95e7-d84704463fff	RADIAN-00002	App crash	App crash	a89aaa73-27e7-4414-b19c-5a85803ae08e	dd959636-dd9d-4b76-8fce-0ae1f81481f0	\N	LOW	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	\N	2026-04-10 22:25:26.762285	2026-04-10 22:25:26.763302
dc905c84-dae5-4783-bfbf-1bfea2ae3509	RADIAN-00003	App crash	App crash	a89aaa73-27e7-4414-b19c-5a85803ae08e	dd959636-dd9d-4b76-8fce-0ae1f81481f0	\N	LOW	OPEN	ae7e617c-b71d-44f2-8322-2f5e85d50a2c	\N	2026-04-10 22:25:36.93965	2026-04-10 22:25:36.93965
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, first_name, last_name, email, password_hash, role, is_active, last_login, created_at, updated_at) FROM stdin;
ae7e617c-b71d-44f2-8322-2f5e85d50a2c	System	Admin	admin@ticketingsys.com	$2a$10$3PJJiqP/DQkpHZXfss5e6OdjZ6E6wWIbOOeMzTd/asdsLCaChKYvK	ADMIN	t	\N	2026-04-10 18:34:46.802329	2026-04-10 18:34:46.802329
9d2d85f3-444e-4150-844b-56418148b517	Yogesh	Nanekar	yogesh@yopmail.com	$2a$10$EC/bEs3hzHjCovC9VvrhGe8/nSFOftwHmctp1M3oztY9ieC7j.cbS	SUPPORT	t	\N	2026-04-10 20:15:43.212009	2026-04-10 20:15:43.212009
\.


--
-- Name: attachments attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_pkey PRIMARY KEY (id);


--
-- Name: categories categories_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_name_key UNIQUE (name);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: client_users client_users_client_id_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_users
    ADD CONSTRAINT client_users_client_id_user_id_key UNIQUE (client_id, user_id);


--
-- Name: client_users client_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_users
    ADD CONSTRAINT client_users_pkey PRIMARY KEY (id);


--
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: projects projects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: projects projects_short_code_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_short_code_unique UNIQUE (short_code);


--
-- Name: ticket_status_history ticket_status_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticket_status_history
    ADD CONSTRAINT ticket_status_history_pkey PRIMARY KEY (id);


--
-- Name: tickets tickets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (id);


--
-- Name: tickets tickets_ticket_number_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_ticket_number_key UNIQUE (ticket_number);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: attachments attachments_comment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_comment_id_fkey FOREIGN KEY (comment_id) REFERENCES public.comments(id) ON DELETE CASCADE;


--
-- Name: attachments attachments_ticket_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_ticket_id_fkey FOREIGN KEY (ticket_id) REFERENCES public.tickets(id) ON DELETE CASCADE;


--
-- Name: attachments attachments_uploaded_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_uploaded_by_fkey FOREIGN KEY (uploaded_by) REFERENCES public.users(id);


--
-- Name: client_users client_users_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_users
    ADD CONSTRAINT client_users_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.clients(id) ON DELETE CASCADE;


--
-- Name: client_users client_users_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.client_users
    ADD CONSTRAINT client_users_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: comments comments_ticket_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_ticket_id_fkey FOREIGN KEY (ticket_id) REFERENCES public.tickets(id) ON DELETE CASCADE;


--
-- Name: comments comments_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: projects projects_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.clients(id) ON DELETE CASCADE;


--
-- Name: ticket_status_history ticket_status_history_changed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticket_status_history
    ADD CONSTRAINT ticket_status_history_changed_by_fkey FOREIGN KEY (changed_by) REFERENCES public.users(id);


--
-- Name: ticket_status_history ticket_status_history_ticket_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ticket_status_history
    ADD CONSTRAINT ticket_status_history_ticket_id_fkey FOREIGN KEY (ticket_id) REFERENCES public.tickets(id) ON DELETE CASCADE;


--
-- Name: tickets tickets_assigned_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_assigned_to_fkey FOREIGN KEY (assigned_to) REFERENCES public.users(id);


--
-- Name: tickets tickets_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: tickets tickets_client_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_client_id_fkey FOREIGN KEY (client_id) REFERENCES public.clients(id);


--
-- Name: tickets tickets_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id);


--
-- Name: tickets tickets_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tickets
    ADD CONSTRAINT tickets_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id);


--
-- PostgreSQL database dump complete
--

\unrestrict aENeXlG9xLnuFOvWsbDHFQ00umlPJFgf83eQP5zO7htIupkkv27SN0szhj30QbZ

