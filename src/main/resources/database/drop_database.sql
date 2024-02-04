DELETE FROM zajavka_store.customer WHERE 1=1;
DELETE FROM zajavka_store.opinion WHERE 1=1;
DELETE FROM zajavka_store.purchase WHERE 1=1;
DELETE FROM zajavka_store.product WHERE 1=1;
DELETE FROM zajavka_store.producer WHERE 1=1;
DELETE FROM zajavka_store.flyway_schema_history WHERE 1=1;

DROP TABLE IF EXISTS zajavka_store.customer CASCADE;
DROP TABLE IF EXISTS zajavka_store.opinion CASCADE;
DROP TABLE IF EXISTS zajavka_store.purchase CASCADE;
DROP TABLE IF EXISTS zajavka_store.product CASCADE;
DROP TABLE IF EXISTS zajavka_store.producer CASCADE;
DROP TABLE IF EXISTS zajavka_store.flyway_schema_history CASCADE;