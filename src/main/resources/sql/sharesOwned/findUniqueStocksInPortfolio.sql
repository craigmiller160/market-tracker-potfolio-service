SELECT DISTINCT symbol
FROM shares_owned
WHERE portfolio_id = :portfolioId
AND user_id = :userId
ORDER BY symbol ASC