CREATE EXTENSION pgcrypto;

CREATE TABLE raw_share_data (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    date DATE NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    total_shares DECIMAL NOT NULL,
    PRIMARY KEY (id)
);