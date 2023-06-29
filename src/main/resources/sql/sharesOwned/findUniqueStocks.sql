SELECT DISTINCT symbol
FROM shares_owned
WHERE user_id = :userId
AND (:portfolioId IS NULL OR portfolio_id = :portfolioId)
AND (:startDate IS NULL OR :startDate::date <= lower(date_range) OR :startDate::date <@ date_range)
AND (:endDate IS NULL OR :endDate::date >= upper(date_range) OR :endDate::date <@ date_range)
AND total_shares > 0
ORDER BY symbol ASC