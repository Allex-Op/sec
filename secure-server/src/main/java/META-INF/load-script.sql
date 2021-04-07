DELETE FROM Proof;
DELETE FROM Report;
DELETE FROM Client;

INSERT INTO Client (identifier, user_id, public_key, is_special_user) VALUES (1, '1', 'iocduhjscfusdc', 0);
INSERT INTO Client (identifier, user_id, public_key, is_special_user) VALUES (2, '2', 'sdfsdfsdfs', 0);
INSERT INTO Client (identifier, user_id, public_key, is_special_user) VALUES (3, '3', '33333333333', 0);
