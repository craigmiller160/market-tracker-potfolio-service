INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares)
VALUES (:id, :userId, :portfolioId, '[:dateRangeStart, :dateRangeEnd)', :symbol, :totalShares);