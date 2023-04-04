SELECT DISTINCT symbol
FROM shares_owned
WHERE user_id = :userId
ORDER BY symbol ASC