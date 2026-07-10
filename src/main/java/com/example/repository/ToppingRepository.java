package com.example.repository;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.domain.Topping;

import lombok.RequiredArgsConstructor;

/**
 * トッピングのripository
 * 
 * @author naramasato
 *
 */
@Repository
@RequiredArgsConstructor
public class ToppingRepository {
	private final NamedParameterJdbcTemplate template;

	private static final RowMapper<Topping> TOPPING_ROW_MAPPER = (rs, i) -> {
		Topping topping = new Topping();
		topping.setId(rs.getInt("id"));
		topping.setName(rs.getString("name"));
		topping.setPriceM(rs.getInt("price_m"));
		topping.setPriceL(rs.getInt("price_l"));
		return topping;
	};

	/**
	 * トッピングを全件探す
	 * 
	 * @return 検索されたトッピングテーブルの情報
	 */
	public List<Topping> findAllTopping() {
		String findAllToppingSql = "SELECT * FROM toppings";
		List<Topping> toppingList = template.query(findAllToppingSql, TOPPING_ROW_MAPPER);
		return toppingList;
	}

}
