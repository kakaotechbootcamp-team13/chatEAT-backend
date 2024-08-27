create table member
(
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(63)  NOT NULL UNIQUE,
    password    VARCHAR(127) NOT NULL,
    nickname    VARCHAR(31) UNIQUE,
    role        VARCHAR(15)  NOT NULL,
    social_type VARCHAR(15),
    social_id   VARCHAR(63),
    is_blocked  TINYINT,
    created_at  TIMESTAMP    NOT NULL
);
