DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE url_checks (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    url_id BIGINT NOT NULL,
    status_code INT NOT NULL,
    title VARCHAR(255),
    h1 VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE url_checks
	ADD CONSTRAINT url_checks_url_id_foreign FOREIGN KEY (url_id) REFERENCES urls(id);
