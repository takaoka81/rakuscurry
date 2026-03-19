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
    public void execute(String targetDate) {
        System.out.println("データの処理を開始します。日付: " + targetDate);

        // 1. Ordersの取得と送信
        List<Order> orders = repository.findOrdersByDate(targetDate);
        sendData(ordersUrl, orders);

        // 2. OrderItemsの取得と送信
        List<OrderItem> items = repository.findOrderItemsByDate(targetDate);
        sendData(orderItemsUrl, items);

        // 3. OrderToppingsの取得と送信
        List<OrderTopping> toppings = repository.findOrderToppingsByDate(targetDate);
        sendData(orderToppingsUrl, toppings);
    }

    /**
     * RestTemplateを使用してJSONデータをPOST送信する共通メソッド
     */
    private void sendData(String url, List<?> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("送信対象のデータがありません URL: " + url);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Jacksonで自動的にListをJSON配列に変換
            HttpEntity<?> entity = new HttpEntity<>(data, headers);

            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("送信成功しました！" + data.size() + " 件のデータを送信しました。URL：" + url);
        } catch (Exception e) {
            System.err.println(" データ送信に失敗しました。URL: " + url + " エラー内容：" + e.getMessage());
        }
    }
}
