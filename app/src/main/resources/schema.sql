DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE url_checks (
    id BIGSERIAL PRIMARY KEY,
    url_id BIGINT NOT NULL,
    status_code INT NOT NULL,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT url_checks_url_id_foreign FOREIGN KEY (url_id) REFERENCES urls(id)
);

