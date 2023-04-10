DELETE FROM shares_owned
WHERE user_id IN (:userIds);