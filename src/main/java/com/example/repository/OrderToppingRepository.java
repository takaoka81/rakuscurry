package com.example.repository;

import java.util.List;

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
	public void insert(List<OrderTopping> orderTopping) {
		SqlParameterSource[] param = orderTopping.stream()
				.map(BeanPropertySqlParameterSource::new)
				.toArray(SqlParameterSource[]::new);
		String insertSql = "INSERT INTO order_toppings (topping_id, order_item_id, order_price) "
				+ "VALUES (:toppingId, :orderItemId, :orderPrice);";
		template.batchUpdate(insertSql, param);
	}
}
