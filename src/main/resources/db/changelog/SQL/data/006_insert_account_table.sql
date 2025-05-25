INSERT INTO ACCOUNT (ID, USER_ID, BALANCE)
VALUES
    (1, 1, 1000.00),
    (2, 2, 2000.00);

SELECT setval('account_id_seq', (SELECT MAX(id) from ACCOUNT));