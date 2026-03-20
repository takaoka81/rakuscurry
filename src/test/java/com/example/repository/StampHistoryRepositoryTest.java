package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.example.domain.StampHistory;

@JdbcTest
@Import(StampHistoryRepository.class)
class StampHistoryRepositoryTest {

    @Autowired
    private StampHistoryRepository stampHistoryRepository;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @BeforeEach
    void setUp() {
        // 子テーブルから順に削除（またはCASCADE）
        template.getJdbcOperations().execute("TRUNCATE TABLE stamp_history RESTART IDENTITY CASCADE");
        template.getJdbcOperations().execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");
        template.getJdbcOperations().execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");

        // 1. usersテーブルにテストデータを挿入 (NOT NULL項目を網羅)
        String userSql = """
                INSERT INTO users (id, name, email, password, zipcode, address, telephone)
                VALUES (1, 'テストユーザー', 'test@example.com', 'password', '123-4567', '東京都新宿区', '03-1234-5678')
                """;
        template.getJdbcOperations().execute(userSql);

        // 2. ordersテーブルにテストデータを挿入 (NOT NULL項目を網羅)
        String orderSql = """
                INSERT INTO orders (id, user_id, status, total_price)
                VALUES (100, 1, 1, 5000)
                """;
        template.getJdbcOperations().execute(orderSql);
    }

    @Nested
    @DisplayName("insertメソッドのテスト")
    class InsertTest {
        @Test
        @DisplayName("正常にデータが登録されること")
        void testInsert() {
            StampHistory history = new StampHistory();
            history.setUserId(1);
            history.setOrderId(100);
            history.setStampCountChanges(10);

            stampHistoryRepository.insert(history);

            // DBから直接取得して検証
            MapSqlParameterSource param = new MapSqlParameterSource().addValue("orderId", 100);
            Integer count = template.queryForObject(
                    "SELECT count(*) FROM stamp_history WHERE order_id = :orderId", param, Integer.class);

            assertEquals(1, count);
        }
    }

    @Nested
    @DisplayName("findByOrderIdメソッドのテスト")
    class FindByOrderIdTest {
        @Test
        @DisplayName("SQLで直接投入したデータがRowMapper経由で取得できること")
        void testFindByOrderId() {
            // 直接データを投入
            template.getJdbcOperations().execute(
                    "INSERT INTO stamp_history (user_id, order_id, stamp_count_changes) VALUES (1, 100, -25)");

            StampHistory result = stampHistoryRepository.findByOrderId(100);

            assertNotNull(result);
            assertEquals(1, result.getUserId());
            assertEquals(100, result.getOrderId());
            assertEquals(-25, result.getStampCountChanges());
        }
    }
}
