SELECT DISTINCT symbol
FROM shares_owned
WHERE portfolio_id = :portfolioId
ORDER BY symbol ASC