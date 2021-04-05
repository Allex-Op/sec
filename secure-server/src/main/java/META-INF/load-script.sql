DELETE FROM Proof;
DELETE FROM Report;
DELETE FROM Client;

INSERT INTO Client (identifier, user_id, public_key) VALUES (1, '1', 'iocduhjscfusdc');
INSERT INTO Client (identifier, user_id, public_key) VALUES (2, '2', 'sdfsdfsdfs');

INSERT INTO Report (identifier, user_identifier, epoch, x, y, digital_signature) VALUES (1, 1, 1, 2, 3, 'asdasdasda');

INSERT INTO Proof (identifier, user_identifier, epoch, report_identifier, digital_signature) VALUES (1, 2, 1, 1, 'dididididid');
