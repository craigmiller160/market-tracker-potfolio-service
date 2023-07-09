INSERT INTO portfolios (id, user_id, name)
VALUES (:portfolio1Id, :user1Id, 'Main Portfolio'),
(:portfolio2Id, :user1Id, 'Other Portfolio'),
(:portfolio3Id, :user2Id, 'Other User Portfolio');

INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares)
VALUES (gen_random_uuid(), :user2Id, :portfolio3Id, '[2023-01-01,2100-01-01)', :stock, 10);
