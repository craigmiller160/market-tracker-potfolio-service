INSERT INTO portfolios (id, user_id, name)
VALUES (gen_random_uuid(), gen_random_uuid(), 'My Portfolio');

INSERT INTO shares_owned (id, user_id, portfolio_id, date, symbol, total_shares)
SELECT gen_random_uuid(), user_id, id, '[2023-01-08, 2023-01-11]'::daterange, 'VTI', 10
FROM portfolios
UNION
SELECT gen_random_uuid(), user_id, id, '[2023-01-10, 2023-01-15]'::daterange, 'VTI', 10
FROM portfolios;

-- Query for a date in one of the ranges
select *
from shares_owned
where '2023-01-15'::date <@ date;

-- Generate the date series
select date_trunc('day', dd)::date
from generate_series('2023-01-01'::date, '2023-02-01'::date, '1 day'::interval) dd;