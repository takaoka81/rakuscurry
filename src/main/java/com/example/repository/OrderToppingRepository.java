package com.example.repository;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.OrderTopping;

import lombok.RequiredArgsConstructor;

/**
 * order_toppingsテーブルとやりとりする
 * 
 * @author naramasato
 *
 */
@Repository
@RequiredArgsConstructor
public class OrderToppingRepository {
	private final NamedParameterJdbcTemplate template;

	/**
	 * 注文トッピングを追加
	 * 
	 * @param orderTopping
	 */
	public void insert(OrderTopping orderTopping) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(orderTopping);
		String insertSql = "INSERT INTO order_toppings (topping_id, order_item_id, order_price) "
				+ "VALUES (:toppingId, :orderItemId, :orderPrice);";
		template.update(insertSql, param);
	}
}
