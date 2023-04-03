ALTER TABLE shares_owned
DROP CONSTRAINT shares_owned_user_id_portfolio_id_date_range_excl;

ALTER TABLE shares_owned
ADD CONSTRAINT shares_owned_user_id_portfolio_id_date_range_excl
EXCLUDE USING GIST (user_id WITH =, portfolio_id WITH =, symbol WITH =, date_range with &&)