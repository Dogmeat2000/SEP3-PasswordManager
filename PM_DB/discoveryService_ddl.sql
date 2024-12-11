SET SCHEMA 'db_discoveryservice';

CREATE TABLE discoveryservice(
    id BIGSERIAL,
    host varchar(260),
    port INT,
    congestion_percentage INT,
    last_ping TIMESTAMP DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
    PRIMARY KEY (id));