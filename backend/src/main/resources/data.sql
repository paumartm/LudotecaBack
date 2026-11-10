INSERT INTO category(name)
VALUES ('Eurogames');
INSERT INTO category(name)
VALUES ('Ameritrash');
INSERT INTO category(name)
VALUES ('Familiar');

INSERT INTO author(name, nationality)
VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality)
VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality)
VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality)
VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality)
VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality)
VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id)
VALUES ('On Mars', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Aventureros al tren', '8', 3, 1);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('1920: Wall Street', '12', 1, 4);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Los viajes de Marco Polo', '12', 1, 3);
INSERT INTO game(title, age, category_id, author_id)
VALUES ('Azul', '8', 3, 5);

INSERT INTO client(name)
VALUES ('Pepe');
INSERT INTO client(name)
VALUES ('Carlos');
INSERT INTO client(name)
VALUES ('Lucia');
INSERT INTO client(name)
VALUES ('Carmen');


INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('1', '2', '2024-12-23', '2025-1-1');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('2', '1', '2025-3-1', '2025-3-20');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('3', '3', '2024-11-3', '2025-11-13');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('4', '4', '2024-9-21', '2025-10-2');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('5', '3', '2025-1-25', '2025-2-4');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('6', '1', '2025-3-2', '2025-3-14');
INSERT INTO loan(game_id, client_id, loan_date, return_date)
VALUES ('2', '2', '2025-3-15', '2025-3-28');