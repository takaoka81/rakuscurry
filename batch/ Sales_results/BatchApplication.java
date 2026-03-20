package com.example.batch;

import com.example.batch.service.DataExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // BatchScheduler の @Scheduled が有効になる。
public class BatchApplication implements CommandLineRunner {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BatchApplication.class);

    @Autowired
    private DataExportService dataExportService;

    public static void main(String[] args) {
        // Spring Boot アプリケーションの起動
        SpringApplication.run(BatchApplication.class, args);
    }

    /**
     * アプリ起動直後に実行されるメソッド
     * 引数 (args) がある場合はその日付で処理を行う。
     */
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            String targetDate = args[0]; // 引数から日付（例：2026-11-28）を取得
            logger.info("手動実行モードで開始します。引数: {}", targetDate);
            System.out.println("--- 実行を開始します。日付: " + targetDate + " ---");
            
            try {
                dataExportService.execute(targetDate);
                System.out.println("--- 実行が正常に終了しました。 ---");
            } catch (Exception e) {
                logger.error("実行中にエラーが発生しました: {}", e.getMessage(), e);
                System.err.println("--- 実行中にエラーが発生しました: " + e.getMessage() + " ---");
            }
        } else {
            logger.info("引数なしで起動中");
            System.out.println("--- バッチアプリケーション起動完了 ---");
        }
    }
}
