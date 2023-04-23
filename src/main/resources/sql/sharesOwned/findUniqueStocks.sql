SELECT DISTINCT symbol
FROM shares_owned
WHERE user_id = :userId
AND (:portfolioId IS NULL OR portfolio_id = :portfolioId)
ORDER BY symbol ASC