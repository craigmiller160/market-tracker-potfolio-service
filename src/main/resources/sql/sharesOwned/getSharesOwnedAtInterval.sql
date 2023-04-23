WITH the_dates AS (
    SELECT date_trunc('day', dd)::date AS the_date
    FROM generate_series(:startDate::date, :endDate::date, :interval::interval) dd
)
SELECT td.the_date AS date, so.user_id, so.symbol, so.total_shares,
CASE
    WHEN :portfolioId IS NOT NULL THEN so.portfolio_id
END
FROM the_dates td
JOIN shares_owned so ON td.the_date <@ so.date_range
WHERE so.user_id = :userId
AND so.symbol = :symbol
AND (:portfolioId IS NULL OR so.portfolio_id = :portfolioId)

-- GROUP BY td.the_date, so.user_id, so.symbol