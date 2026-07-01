package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.StampHistory;

@SpringBootTest
@Transactional
@Sql("/sql/StampHistoryRepositoryTestData.sql")
class StampHistoryRepositoryTest {

    @Autowired
    private StampHistoryRepository stampHistoryRepository;

    @Autowired
    private NamedParameterJdbcTemplate template;

    @Test
    @DisplayName("正常にデータが登録されること")
    void testInsert() {
        // DBから直接取得して検証
        MapSqlParameterSource param = new MapSqlParameterSource().addValue("orderId", 1);
        Integer count = template.queryForObject(
                "SELECT count(*) FROM stamp_history WHERE order_id = :orderId", param, Integer.class);

        assertEquals(1, count);

    }

    @Test
    @DisplayName("SQLで直接投入したデータがRowMapper経由で取得できること")
    void testFindByOrderId() {
        StampHistory result = stampHistoryRepository.findByOrderId(1);

        assertNotNull(result);
        assertEquals(50, result.getUserId());
        assertEquals(1, result.getOrderId());
        assertEquals(1, result.getStampCountChanges());
    }

}
