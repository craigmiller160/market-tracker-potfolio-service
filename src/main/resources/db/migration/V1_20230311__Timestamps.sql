ALTER TABLE portfolios
ADD COLUMN insert_timestamp TIMESTAMP NOT NULL;

ALTER TABLE portfolios
ADD COLUMN update_timestamp TIMESTAMP NOT NULL;

ALTER TABLE shares_owned
ADD COLUMN insert_timestamp TIMESTAMP NOT NULL;

ALTER TABLE shares_owned
ADD COLUMN update_timestamp TIMESTAMP NOT NULL;