INSERT INTO shares_owned (id, user_id, portfolio_id, date_range, symbol, total_shares)
VALUES (gen_random_uuid(), :user2Id, :portfolio3Id, '[2023-01-01,2100-01-01)', 'VTI', 10),
(gen_random_uuid(), :user1Id, :portfolio1Id, '[2023-01-01,2023-02-01)', 'VTI', '5'),
(gen_random_uuid(), :user1Id, :portfolio1Id, '[2023-02-01-2023-03-01)', 'VTI', '10'),
(gen_random_uuid(), :user1Id, :portfolio1Id, '[2023-03-01,2100-01-01)', 'VTI', '15'),
(gen_random_uuid(), :user1Id, :portfolio2Id, '[2023-01-01,2023-02-01)', 'VTI', '6'),
(gen_random_uuid(), :user1Id, :portfolio2Id, '[2023-02-01-2023-03-01)', 'VTI', '11'),
(gen_random_uuid(), :user1Id, :portfolio2Id, '[2023-03-01,2100-01-01)', 'VTI', '16');