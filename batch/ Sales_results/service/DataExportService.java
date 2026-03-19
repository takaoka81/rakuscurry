package com.example.batch.service;

import com.example.batch.entity.Order;
import com.example.batch.entity.OrderItem;
import com.example.batch.entity.OrderTopping;
import com.example.batch.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DataExportService {

     private static final Logger logger = LoggerFactory.getLogger(DataExportService.class);

    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    // application.propertiesからURLを読み込み
    @Value("${api.url.orders}")
    private String ordersUrl;

    @Value("${api.url.order-items}")
    private String orderItemsUrl;

    @Value("${api.url.order-toppings}")
    private String orderToppingsUrl;

    /**
     * メイン処理: 指定された日付のデータを抽出し、外部システムへ送信
     */
    @Transactional(readOnly = true)
    public void execute(String targetDate) {
        logger.info("データ抽出・送信処理を開始します。対象日: {}", targetDate);
        System.out.println("データの処理を開始します。日付: " + targetDate);

 try {
            // 1. Orders
            sendData(ordersUrl, repository.findOrdersByDate(targetDate), "注文基本");

            // 2. OrderItems
            sendData(orderItemsUrl, repository.findOrderItemsByDate(targetDate), "注文商品");

            // 3. OrderToppings
            sendData(orderToppingsUrl, repository.findOrderToppingsByDate(targetDate), "注文トッピング");

            logger.info("全データの処理が正常に完了しました。");
        } catch (Exception e) {
            logger.error("バッチ処理中に予期せぬエラーが発生しました: {}", e.getMessage(), e);
        }
    }

    /**
     * RestTemplateを使用してJSONデータをPOST送信する共通メソッド
     */
    private void sendData(String url, List<?> data,String dataName) {
        if (data == null || data.isEmpty()) {
            logger.info("送信対象データがありません。種別: {}, URL: {}", dataName, url);
            System.out.println("送信対象のデータがありません URL: " + url);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Jacksonで自動的にListをJSON配列に変換
            HttpEntity<?> entity = new HttpEntity<>(data, headers);

            restTemplate.postForEntity(url, entity, String.class);

            logger.info("送信成功: {} ({}件) URL: {}", dataName, data.size(), url);
            System.out.println("送信成功しました！" + data.size() + " 件のデータを送信しました。URL：" + url);

        } catch (Exception e) {
            logger.error("データ送信失敗: {} URL: {} 原因: {}", dataName, url, e.getMessage());
            System.err.println(" データ送信に失敗しました。URL: " + url + " エラー内容：" + e.getMessage());
        }
    }
}
