CREATE TABLE IF NOT EXISTS clients
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    balance DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS employees
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    birth_date DATE,
    phone VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS books
(
    id BIGSERIAL PRIMARY KEY,
    name_en VARCHAR(255) NOT NULL,
    name_uk VARCHAR(255) NOT NULL,
    author_en VARCHAR(255) NOT NULL,
    author_uk VARCHAR(255) NOT NULL,
    description_en TEXT,
    description_uk TEXT,
    genre VARCHAR(50) NOT NULL,
    age_group VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL,
    publication_date DATE NOT NULL,
    pages INTEGER NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders
(
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    employee_id BIGINT REFERENCES employees(id),
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    price DECIMAL(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS book_items
(
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id),
    order_id BIGINT NOT NULL REFERENCES orders(id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);
