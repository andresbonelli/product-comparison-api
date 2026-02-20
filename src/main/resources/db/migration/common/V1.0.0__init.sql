CREATE TABLE IF NOT EXISTS product (
         id             BIGINT AUTO_INCREMENT   PRIMARY KEY,
         name           VARCHAR(255)            NOT NULL,
         image_url      VARCHAR(255)            NOT NULL,
         description    VARCHAR(500)            NOT NULL,
         price          DECIMAL(19, 2)          NOT NULL,
         rating         DOUBLE PRECISION        NOT NULL,
         specifications VARCHAR(1000)           NOT NULL
);