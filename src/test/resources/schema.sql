CREATE TABLE users(
	id smallint UNSIGNED AUTO_INCREMENT,
    username VARCHAR(40),
    password VARCHAR(64),
    PRIMARY KEY (id)
);
CREATE TABLE categories(
	id smallint UNSIGNED AUTO_INCREMENT,
	name VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE TABLE status(
	id smallint UNSIGNED AUTO_INCREMENT,
	name VARCHAR(20),
    PRIMARY KEY (id)
);
CREATE TABLE tracker(
	id int UNSIGNED AUTO_INCREMENT,
    name VARCHAR(100),
    category smallint UNSIGNED,
    status smallint UNSIGNED,
    progress varchar(40),
    created_on DATETIME,
    last_read DATETIME,
    PRIMARY KEY (id),
    FOREIGN KEY (category) REFERENCES categories(id),
    FOREIGN KEY (status) REFERENCES status(id)
);