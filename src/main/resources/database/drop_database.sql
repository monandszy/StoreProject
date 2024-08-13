DELETE
FROM spring_store.customer
WHERE 1 = 1;
DELETE
FROM spring_store.opinion
WHERE 1 = 1;
DELETE
FROM spring_store.purchase
WHERE 1 = 1;
DELETE
FROM spring_store.product
WHERE 1 = 1;
DELETE
FROM spring_store.producer
WHERE 1 = 1;
DELETE
FROM spring_store.flyway_schema_history
WHERE 1 = 1;

DROP TABLE IF EXISTS spring_store.customer CASCADE;
DROP TABLE IF EXISTS spring_store.opinion CASCADE;
DROP TABLE IF EXISTS spring_store.purchase CASCADE;
DROP TABLE IF EXISTS spring_store.product CASCADE;
DROP TABLE IF EXISTS spring_store.producer CASCADE;
DROP TABLE IF EXISTS spring_store.flyway_schema_history CASCADE;