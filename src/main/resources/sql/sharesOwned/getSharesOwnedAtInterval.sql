WITH the_dates AS (
    SELECT date_trunc('day', dd)::date AS the_date
    FROM generate_series(:startDate::date, :endDate::date, :interval::interval) dd
)
SELECT
{{#portfolioId}}
so.portfolio_id, so.total_shares,
{{/portfolioId}}
{{^portfolioId}}
SUM(so.total_shares) AS total_shares,
{{/portfolioId}}
td.the_date AS date, so.user_id, so.symbol
FROM the_dates td
JOIN shares_owned so ON td.the_date <@ so.date_range
WHERE so.user_id = :userId
AND so.symbol = :symbol
{{#portfolioId}}
AND so.portfolio_id = :portfolioId
{{/portfolioId}}
{{^portfolioId}}
GROUP BY td.the_date, so.user_id, so.symbol
{{/portfolioId}}