package com.example.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * アプリケーション起動時にDBデータを表記
 */
@Component
public class datedasecheck implements CommandLineRunner{

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DateBaseCheck.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("データベース実績データ確認 (起動時実行)");
        try {
            String sql = "SELECT id, user_id, status, total_price, order_date, destination_name,"
             + "destiation_email, destination_zipcode, destination_address, destination_tel,"
             + "o_delivery_time, payment_method" 
             + "FROM orders ORDER BY id DESC LIMIT 10";
            
            // SQL実行
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            if (rows.isEmpty()) {
                logger.warn("データが存在しません。");
            } else {
                logger.info("最新 {} 件のデータをコンソールに表示しました。", rows.size());
            }
            
            // 件数の確認
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM orders", Integer.class);
            logger.info("起動時の総レコード数: {} 件", count);

        } catch (Exception e) {
            logger.error("データ確認中にエラーが発生しました: {}", e.getMessage());
        }
    }
}
