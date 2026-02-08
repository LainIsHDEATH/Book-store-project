CREATE TABLE IF NOT EXISTS books
(
    id                BIGSERIAL PRIMARY KEY,
    name_en           VARCHAR(255) NOT NULL,
    name_uk           VARCHAR(255) NOT NULL,
    genre_en          VARCHAR(255),
    genre_uk          VARCHAR(100),
    age_group         VARCHAR(50),
    price             DECIMAL(10, 2) NOT NULL,
    publication_date  DATE,
    author_en         VARCHAR(255),
    author_uk         VARCHAR(255),
    pages             INTEGER,
    characteristic    TEXT,
    description_en    TEXT,
    description_uk    TEXT,
    stock_count       INT DEFAULT 0,
    language          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS employees
(
    id         BIGSERIAL PRIMARY KEY,
    birth_date DATE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255),
    is_blocked BOOLEAN DEFAULT FALSE,
    password   VARCHAR(255) NOT NULL,
    phone      VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS clients
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    name            VARCHAR(255),
    balance         DECIMAL(10, 2) DEFAULT 0.00,
    is_blocked      BOOLEAN DEFAULT FALSE,
    password        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    id          BIGSERIAL PRIMARY KEY,
    client_id   BIGINT REFERENCES clients (id),
    employee_id BIGINT REFERENCES employees (id),
    order_date  TIMESTAMP WITHOUT TIME ZONE,
    price       DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS admins
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL
);