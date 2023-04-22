SELECT so.total_shares
FROM shares_owned so
WHERE so.symbol = :symbol
AND so.user_id = :userId
AND (:portfolioId IS NULL OR so.portfolio_id = :portfolioId)
ORDER BY so.date_range DESC
LIMIT 1;