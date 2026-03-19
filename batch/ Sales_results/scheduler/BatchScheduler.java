package com.example.batch.scheduler;

import com.example.batch.service.DataExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BatchScheduler {

    @Autowired
    private DataExportService dataExportService;

    /**
     * 定期実行（Cron）用メソッド
     * 毎日深夜 02:00 に前日分のデータを集計・送信する設定例
     */
    @Scheduled(cron = "${batch.cron.expression:0 0 2 * * *}")
    public void scheduleDailyTask() {
        String targetDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        System.out.println("定期タスクの開始。日付: " + targetDate);
        
        // 共通のサービスメソッドを呼び出し
        dataExportService.execute(targetDate);
        
        System.out.println("定期タスクが正常に完了しました。");
    }
}
