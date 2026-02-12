--liquibase formatted sql

--changeset ivan:1
CREATE TABLE IF NOT EXISTS clients
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    is_blocked BOOLEAN DEFAULT FALSE,
    balance DECIMAL(12,2) DEFAULT 0.00
);

--changeset ivan:2
CREATE TABLE IF NOT EXISTS employees
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    is_blocked BOOLEAN DEFAULT FALSE,
    birth_date DATE,
    phone VARCHAR(30)
);

--changeset ivan:3
CREATE TABLE IF NOT EXISTS books
(
    id BIGSERIAL PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_uk VARCHAR(255) NOT NULL,
    author_en VARCHAR(255),
    author_uk VARCHAR(255),
    description_en TEXT,
    description_uk TEXT,
    genre_en VARCHAR(100),
    genre_uk VARCHAR(100),
    age_group VARCHAR(50),
    language VARCHAR(50),
    characteristics TEXT,
    publication_date DATE,
    pages INTEGER,
    price DECIMAL(12,2) NOT NULL,
    stock_count INTEGER DEFAULT 0
);

--changeset ivan:4
CREATE TABLE IF NOT EXISTS orders
(
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    employee_id BIGINT REFERENCES employees(id),
    order_date TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    price DECIMAL(12,2) NOT NULL
);

--changeset ivan:5
CREATE TABLE IF NOT EXISTS book_items
(
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE ,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE ,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL
);

--changeset ivan:6
CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);

--changeset ivan:7
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);
