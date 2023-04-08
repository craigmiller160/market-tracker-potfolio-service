SELECT DISTINCT symbol
FROM shares_owned
WHERE user_id = :userId
{{#portfolioId}}
AND portfolio_id = :portfolioId
{{/portfolioId}}
ORDER BY symbol ASC