SELECT SUM(a.total_shares) AS the_sum
FROM (
    SELECT
    {{^portfolioId}}
    so.portfolio_id,
    {{/portfolioId}}
    so.total_shares, MAX(LOWER(so.date_range))
    FROM shares_owned so
    WHERE so.user_id = :userId
    {{#portfolioId}}
    AND so.portfolio_id = :portfolioId
    {{/portfolioId}}
    AND so.symbol = :symbol
    GROUP BY
    {{^portfolioId}}
    so.portfolio_id,
    {{/portfolioId}}
    so.total_shares
) a;