INSERT INTO PHONE_DATA (ID, USER_ID, PHONE)
VALUES
    (1, 1, '123456789012'),
    (2, 2, '109876543210');

SELECT setval('phone_data_id_seq', (SELECT MAX(id) from PHONE_DATA));