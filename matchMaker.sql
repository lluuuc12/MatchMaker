DROP TABLE IF EXISTS Persons_Hobbies;
DROP TABLE IF EXISTS Hobbies;
DROP TABLE IF EXISTS Persons;

CREATE TABLE Persons (
    cod_person SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    age INT,
    photo bytea default null
);

CREATE TABLE Hobbies (
    cod_hobby SERIAL PRIMARY KEY,
    hobby_name VARCHAR(255)
);

INSERT INTO Hobbies (hobby_name) VALUES
    ('Sports'),
    ('Music'),
    ('Reading'),
    ('Traveling'),
    ('Cooking'),
    ('Movies'),
    ('Art'),
    ('Games');

CREATE TABLE Persons_Hobbies (
    cod_person INT REFERENCES Persons(cod_person) ON UPDATE CASCADE ON DELETE CASCADE,
    cod_hobby INT REFERENCES Hobbies(cod_hobby),
    PRIMARY KEY (cod_person, cod_hobby)
);
