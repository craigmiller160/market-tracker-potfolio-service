-- https://www.postgresql.org/docs/current/rangetypes.html
CREATE EXTENSION pgcrypto;
CREATE EXTENSION btree_gist;

CREATE TABLE portfolios (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX ON portfolios (user_id);

CREATE TABLE shares_owned (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    portfolio_id UUID NOT NULL,
    -- TODO need an index, does the GIST auto-apply an index? I don't think so
    date DATERANGE NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    total_shares DECIMAL NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (portfolio_id) REFERENCES portfolios (id),
    EXCLUDE USING GIST (user_id WITH =, portfolio_id WITH =, date WITH &&)
);
CREATE INDEX ON shares_owned (user_id);
CREATE INDEX ON shares_owned (portfolio_id);