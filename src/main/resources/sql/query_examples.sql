INSERT INTO portfolios (id, user_id, name)
VALUES (gen_random_uuid(), gen_random_uuid(), 'My Portfolio');

INSERT INTO shares_owned (id, user_id, portfolio_id, date, symbol, total_shares)
SELECT gen_random_uuid(), user_id, id, '[2023-01-08, 2023-01-11]'::daterange, 'VTI', 10
FROM portfolios
UNION
SELECT gen_random_uuid(), user_id, id, '[2023-01-10, 2023-01-15]'::daterange, 'VTI', 10
FROM portfolios;

select *
from shares_owned
where '2023-01-15'::date <@ date;