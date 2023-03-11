INSERT INTO portfolios (id, user_id, name)
VALUES (gen_random_uuid(), gen_random_uuid(), 'My Portfolio');

INSERT INTO shares_owned (id, user_id, portfolio_id, date, symbol, total_shares)
SELECT gen_random_uuid(), user_id, id, '[2023-01-01, 2023-01-09]'::daterange, 'VTI', 10
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

-- CTE that generates the date range and then does the query
with the_dates as (
    select date_trunc('day', dd)::date as the_date
    from generate_series('2023-01-01'::date, '2023-02-01'::date, '1 day'::interval) dd
)
select td.the_date, so.*
from the_dates td
-- Will end after the max date in shares_owned, even if I don't want that
join shares_owned so on td.the_date <@ so.date;
