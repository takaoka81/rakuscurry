-- ユーザー情報
INSERT INTO users (id, name, email, password, zipcode, address, telephone,status) 
VALUES (50, 'テスト太郎', 'test@test', 'Testtest', 'testtest', '東京都新宿区', '000-0000-0000', 0);

-- 注文データ
INSERT INTO orders (id, user_id, status, total_price) 
VALUES (1, 50, 0, 11000);

INSERT INTO stamp_history(user_id, order_id, stamp_count_changes)
VALUES (50, 1 , 1);
