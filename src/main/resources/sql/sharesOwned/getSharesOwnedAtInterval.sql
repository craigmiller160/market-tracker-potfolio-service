WITH the_dates AS (
    SELECT date_trunc('day', dd)::date AS the_date
    FROM generate_series(:startDate::date, :endDate::date, :interval::interval) dd
)
SELECT
{{#portfolioId}}
so.portfolio_id,
{{/portfolioId}}
td.the_date AS date, so.user_id, so.symbol, so.total_shares
FROM the_dates td
JOIN shares_owned so ON td.the_date <@ so.date_range
WHERE so.user_id = :userId
{{#portfolioId}}
AND so.portfolio_id = :portfolioId
{{/portfolioId}}