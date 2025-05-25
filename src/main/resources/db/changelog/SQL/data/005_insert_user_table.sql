INSERT INTO USERS (ID, NAME, DATE_OF_BIRTH, PASSWORD)
VALUES
    (1, 'Alice', '1990-01-01', '$2a$10$zkYU8OBHU6k6QfV4GfiS4ePFFRVLGGXl8FKcBa.IauFVKvI.RxzM6'),
    (2, 'Bob', '1985-05-05', '$2a$10$7IaOp72u1JesKnTiQyiUSOftvwKetuGlY/UT0KALw5h/xxmjQKka2');

SELECT setval('user_id_seq', (SELECT MAX(id) from USERS));

--Passwords: 1/12345678
--           2/super-password