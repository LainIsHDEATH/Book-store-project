--liquibase formatted sql

--changeset ivan:1
INSERT INTO employees (birth_date, email, name, password, phone, is_blocked)
VALUES
    ('1990-10-10', 'employee@book.com', 'Employee User', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiQdP7Pj5A3M9x8xwV4tP5xA5R2rY6m', '+380931111111', false),
    ('1990-05-15', 'john.doe@email.com', 'John Doe', 'pass123', '555-123-4567', false),
    ('1985-09-20', 'jane.smith@email.com', 'Jane Smith', 'abc456', '555-987-6543', false),
    ('1978-03-08', 'bob.jones@email.com', 'Bob Jones', 'qwerty789', '555-321-6789', false),
    ('1982-11-25', 'alice.white@email.com', 'Alice White', 'secret567', '555-876-5432', false),
    ('1995-07-12', 'mike.wilson@email.com', 'Mike Wilson', 'mypassword', '555-234-5678', false),
    ('1989-01-30', 'sara.brown@email.com', 'Sara Brown', 'letmein123', '555-876-5433', false),
    ('1975-06-18', 'tom.jenkins@email.com', 'Tom Jenkins', 'pass4321', '555-345-6789', false),
    ('1987-12-04', 'lisa.taylor@email.com', 'Lisa Taylor', 'securepwd', '555-789-0123', false),
    ('1992-08-22', 'david.wright@email.com', 'David Wright', 'access123', '555-456-7890', false),
    ('1980-04-10', 'emily.harris@email.com', 'Emily Harris', '1234abcd', '555-098-7654', false);

--changeset ivan:2
INSERT INTO clients (email, password, name, is_blocked, balance)
VALUES
    ('client1@example.com', 'password123', 'Medelyn Wright', false, 1000.00),
    ('client2@example.com', 'securepass', 'Landon Phillips', false, 1500.50),
    ('client3@example.com', 'abc123', 'Harmony Mason', false, 800.75),
    ('client4@example.com', 'pass456', 'Archer Harper', false, 1200.25),
    ('client5@example.com', 'letmein789', 'Kira Jacobs', false, 900.80),
    ('client6@example.com', 'adminpass', 'Maximus Kelly', false, 1100.60),
    ('client7@example.com', 'mypassword', 'Sierra Mitchell', false, 1300.45),
    ('client8@example.com', 'test123', 'Quinton Saunders', false, 950.30),
    ('client9@example.com', 'qwerty123', 'Amina Clarke', false, 1050.90),
    ('client10@example.com', 'pass789', 'Bryson Chavez', false, 880.20);

--changeset ivan:3
INSERT INTO books (name_en, name_uk, author_en, author_uk, description_en, description_uk, genre, age_group, language, publication_date, pages, price, stock_count)
VALUES
('The Hidden Treasure', 'Прихований скарб', 'Emily White', 'Емілі Вайт', 'Adventure story', 'Пригодницька історія', 'ADVENTURE', 'TEEN', 'ENGLISH', '2018-05-15', 320, 24.99, 10),
('Echoes of Eternity', 'Відлуння вічності', 'Daniel Black', 'Деніел Блек', 'Fantasy story', 'Фентезі історія', 'FANTASY', 'ADULT', 'UKRAINIAN', '2011-01-15', 350, 16.50, 8),
('Whispers in the Shadows', 'Шепіт у тінях', 'Sophia Green', 'Софія Ґрін','A gripping mystery that keeps you guessing', 'Захоплива детективна історія, що тримає в напрузі','MYSTERY', 'ADULT', 'ENGLISH', '2018-08-11', 450, 29.95, 10),
('The Starlight Sonata', 'Соната зоряного світла', 'Michael Rose', 'Майкл Роуз',
 'A beautiful journey of love and passion', 'Прекрасна подорож кохання та пристрасті',
 'ROMANCE', 'ADULT', 'ENGLISH', '2011-05-15', 320, 21.75, 10),
('Beyond the Horizon', 'За обрієм', 'Alex Carter', 'Алекс Картер',
 'An epic sci-fi adventure beyond the stars', 'Епічна науково-фантастична пригода за межами зірок',
 'SCIENCE_FICTION', 'CHILD', 'ENGLISH', '2004-05-15', 280, 18.99, 10),
('Dancing with Shadows', 'Танець із тінями', 'Olivia Smith', 'Олівія Сміт',
 'A thrilling tale of danger and intrigue', 'Захоплива історія небезпеки та інтриг',
 'THRILLER', 'ADULT', 'ENGLISH', '2015-05-15', 380, 26.50, 10),
('Voices in the Wind', 'Голоси у вітрі', 'William Turner', 'Вільям Тернер',
 'A compelling journey through time', 'Захоплива подорож крізь час',
 'HISTORICAL_FICTION', 'ADULT', 'ENGLISH', '2017-05-15', 500, 32.00, 10),
('Serenade of Souls', 'Серенада душ', 'Isabella Reed', 'Ізабелла Рід',
 'A magical fantasy filled with wonder', 'Магічне фентезі, сповнене див',
 'FANTASY', 'TEEN', 'ENGLISH', '2013-05-15', 330, 15.99, 10),
('Silent Whispers', 'Тихий шепіт', 'Benjamin Hall', 'Бенджамін Голл',
 'A mystery that keeps you on the edge', 'Детектив, що тримає на межі напруги',
 'MYSTERY', 'ADULT', 'ENGLISH', '2021-05-15', 420, 27.50, 10),
('Whirlwind Romance', 'Вир кохання', 'Emma Turner', 'Емма Тернер',
 'A romance that sweeps you off your feet', 'Роман, що зносить з ніг',
 'ROMANCE', 'OTHER', 'ENGLISH', '2022-05-15', 360, 23.25, 10);
