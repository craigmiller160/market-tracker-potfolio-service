-- Intended to be used with batch inserts that don't support named parameters
INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares)
VALUES ($1, $2, $3, $4::daterange, $5, $6);