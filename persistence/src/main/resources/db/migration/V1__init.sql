-- Таблиця користувачів
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255),
                       role VARCHAR(50)
);

-- Таблиця книг
CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       isbn VARCHAR(50),
                       publication_year INT
);

-- Таблиця коментарів (зв'язок Many-to-One до Users та Books)
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          text VARCHAR(2000),
                          created_at TIMESTAMP,
                          user_id BIGINT,
                          book_id BIGINT,
                          CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT fk_comments_book FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Початкові дані (опціонально, для зручності)
INSERT INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN');
INSERT INTO users (username, password, role) VALUES ('user1', 'pass', 'USER');

INSERT INTO books (title, author, isbn, publication_year) VALUES ('Spring in Action', 'Craig Walls', '9781617294945', 2022);

INSERT INTO comments (text, created_at, user_id, book_id) VALUES ('Чудова книга! Дуже рекомендую.', CURRENT_TIMESTAMP(), 1, 1);
INSERT INTO comments (text, created_at, user_id, book_id) VALUES ('Трохи застаріла інформація, але цікаво.', CURRENT_TIMESTAMP(), 1, 1);