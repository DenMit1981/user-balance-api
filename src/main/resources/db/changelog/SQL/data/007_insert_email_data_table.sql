INSERT INTO EMAIL_DATA (ID, USER_ID, EMAIL)
VALUES
    (1, 1, 'alice@example.com'),
    (2, 2, 'bob@example.com');

SELECT setval('email_data_id_seq', (SELECT MAX(id) from EMAIL_DATA));