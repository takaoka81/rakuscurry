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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==========================================");
        System.out.println("   データベース実績データ確認 (起動時実行)   ");
        System.out.println("==========================================");

        try {
            String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 10";
            
            // SQL実行
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            if (rows.isEmpty()) {
                System.out.println("データが存在しません。");
            } else {
                // 取得したデータをループして表示
                for (Map<String, Object> row : rows) {
                    System.out.println(row);
                }
            }
            
            // 件数の確認
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
            System.out.println("------------------------------------------");
            System.out.println("総件数: " + count + " 件");

        } catch (Exception e) {
            System.err.println("データ取得中にエラーが発生しました: " + e.getMessage());
        }

        System.out.println("==========================================");
    }
}
