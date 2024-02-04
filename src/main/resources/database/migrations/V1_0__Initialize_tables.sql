CREATE TABLE zajavka_store.customer
(
    id            SERIAL PRIMARY KEY NOT NULL,
    user_name     TEXT UNIQUE        NOT NULL,
    email         TEXT UNIQUE        NOT NULL,
    name          TEXT,
    surname       TEXT,
    date_of_birth DATE
);

CREATE TABLE zajavka_store.producer
(
    id      SERIAL PRIMARY KEY NOT NULL,
    name    TEXT UNIQUE        NOT NULL,
    address TEXT
);

CREATE TABLE zajavka_store.product
(
    id          SERIAL PRIMARY KEY NOT NULL,
    code        TEXT UNIQUE        NOT NULL,
    name        TEXT               NOT NULL,
    price       NUMERIC(10, 2)     NOT NULL,
    adults_only BOOLEAN            NOT NULL,
    description TEXT               NOT NULL,
    producer_id INT                NOT NULL,
    CONSTRAINT fk_producer
        FOREIGN KEY (producer_id)
            REFERENCES zajavka_store.producer (id)
);


CREATE TABLE zajavka_store.purchase
(
    id               SERIAL PRIMARY KEY       NOT NULL,
    customer_id      INT                      NOT NULL,
    product_id       INT                      NOT NULL,
    quantity         INT                      NOT NULL,
    time_of_purchase TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
            REFERENCES zajavka_store.customer (id),
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES zajavka_store.product (id)
);


CREATE TABLE zajavka_store.opinion
(
    id              SERIAL PRIMARY KEY       NOT NULL,
    customer_id     INT                      NOT NULL,
    product_id      INT                      NOT NULL,
    stars           INTEGER                  NOT NULL,
    comment         TEXT                     NOT NULL,
    time_of_comment TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT check_types
        CHECK (stars IN (1, 2, 3, 4, 5)),
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
            REFERENCES zajavka_store.customer (id),
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES zajavka_store.product (id)
);