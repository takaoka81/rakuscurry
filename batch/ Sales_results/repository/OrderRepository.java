package com.example.batch.repository;

import com.example.batch.entity.Order;
import com.example.batch.entity.OrderItem;
import com.example.batch.entity.OrderTopping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 指定された日付の注文データをすべて取得
     */
    public List<Order> findOrdersByDate(String targetDate) {
        String sql = "SELECT id, user_id, status, total_price, order_date, " +
                     "destination_name, destination_email, destination_zipcode, " +
                     "destination_address, destination_tel, delivery_time, payment_method " +
                     "FROM orders WHERE order_date = CAST(? AS DATE)";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), targetDate);
    }

    /**
     * 指定された日付の注文に紐づく注文商品をすべて取得
     */
    public List<OrderItem> findOrderItemsByDate(String targetDate) {
        String sql = "SELECT oi.id, oi.item_id, oi.order_id, oi.quantity, oi.size " +
                     "FROM order_items oi " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "WHERE o.order_date = CAST(? AS DATE)";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderItem.class), targetDate);
    }

    /**
     * 指定された日付の注文に紐づく注文トッピングをすべて取得
     */
    public List<OrderTopping> findOrderToppingsByDate(String targetDate) {
        String sql = "SELECT ot.id, ot.topping_id, ot.order_item_id " +
                     "FROM order_toppings ot " +
                     "JOIN order_items oi ON ot.order_item_id = oi.id " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "WHERE o.order_date = CAST(? AS DATE)";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OrderTopping.class), targetDate);
    }
}
