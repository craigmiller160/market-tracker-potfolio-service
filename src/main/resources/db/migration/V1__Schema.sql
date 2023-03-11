CREATE EXTENSION pgcrypto;

CREATE TABLE portfolios (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE shares_owned (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    portfolio_id UUID NOT NULL,
    date DATE NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    total_shares DECIMAL NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (portfolio_id) REFERENCES portfolios (id)
);