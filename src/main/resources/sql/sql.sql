INSERT INTO employees (email, password, name, is_blocked, birth_date, phone)
VALUES ('employee@book.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiQdP7Pj5A3M9x8xwV4tP5xA5R2rY6m', 'Employee User', false, '1990-10-10', '+380931111111')
ON CONFLICT (email) DO NOTHING;

INSERT INTO clients (email, password, name, is_blocked, balance)
VALUES ('client@book.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiQdP7Pj5A3M9x8xwV4tP5xA5R2rY6m', 'Client User', false, 1000)
ON CONFLICT (email) DO NOTHING;

INSERT INTO books (name_en, name_uk, author_en, author_uk, description_en, description_uk, genre, age_group, language, publication_date, pages, price, stock_count)
VALUES
('The Hidden Treasure', 'Прихований скарб', 'Emily White', 'Емілі Вайт', 'Adventure story', 'Пригодницька історія', 'ADVENTURE', 'TEEN', 'ENGLISH', '2018-05-15', 320, 24.99, 10),
('Echoes of Eternity', 'Відлуння вічності', 'Daniel Black', 'Деніел Блек', 'Fantasy story', 'Фентезі історія', 'FANTASY', 'ADULT', 'UKRAINIAN', '2011-01-15', 350, 16.50, 8)
ON CONFLICT DO NOTHING;
