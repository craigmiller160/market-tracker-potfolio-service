SELECT SUM(so.total_shares)
FROM shares_owned so
WHERE so.symbol = :symbol
AND UPPER(date_range) = :maxDate
AND so.user_id = :userId
AND (:portfolioId IS NULL OR so.portfolio_id = :portfolioId);