DROP SCHEMA password_manager CASCADE;
CREATE SCHEMA IF NOT EXISTS password_manager;
SET SCHEMA 'password_manager';

CREATE TABLE "master_user"
(
    id SERIAL PRIMARY KEY,
    master_username varchar not null,
    master_password varchar not null
);

CREATE TABLE category
(
    id SERIAL PRIMARY KEY,
    category_name varchar not null
);

CREATE TABLE login_entry
(
    id SERIAL PRIMARY KEY,
    entry_username varchar not null,
    entry_password varchar not null,
    entry_name varchar not null,
    entry_address varchar not null,
    entry_category_id int not null,
    master_user_id int not null,

    FOREIGN KEY (entry_category_id) references category(id),
    FOREIGN KEY (master_user_id) references "master_user"(id)
);