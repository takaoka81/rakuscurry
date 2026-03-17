package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.example.domain.StampHistory;

/**
 * stamp_historyを操作するrepositoryクラス
 *
 * @author masashi.saito
 */
public class StampHistoryRepository {

    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final RowMapper<StampHistory> STAMP_HISTORY_ROW_MAPPER = (rs, i) -> {
        StampHistory stampHistory = new StampHistory();
        stampHistory.setId(rs.getInt("id"));
        stampHistory.setUserId(rs.getInt("user_id"));
        stampHistory.setOrderId(rs.getInt("order_id"));
        stampHistory.setStampCountChenges(rs.getInt("stamp_count_chenges"));
        return stampHistory;
    };

    /**
     * 注文idから検索を行います(キャンセルが実装された時用の処理)
     * 
     * @param orderId 注文id
     * @return 検索条件に該当するカラム
     */
    public StampHistory findByOrderId(Integer orderId) {
        String sql = """
                SELECT
                    id,
                    user_id,
                    order_id,
                    stamp_count_chenges
                FROM
                    stamp_history
                WHERE
                    order_id=:orderId
                """;
        SqlParameterSource param = new MapSqlParameterSource().addValue("order_id", orderId);

        StampHistory stampHistory = template.queryForObject(sql, param, STAMP_HISTORY_ROW_MAPPER);
        return stampHistory;
    }

    /**
     * stamp_historyに対してデータを登録します。
     * 
     * @param stampHistory 登録する情報
     */
    public void insert(StampHistory stampHistory) {
        String sql = """
                INSERT INTO
                    stamp_history(
                        user_id,
                        order_id,
                        stamp_count_chenges
                        )
                VALUES(
                    :userId,
                    :orderId,
                    :stampCountChenges
                );
                """;
        SqlParameterSource param = new BeanPropertySqlParameterSource(stampHistory);
        template.update(sql, param);
    }
}
