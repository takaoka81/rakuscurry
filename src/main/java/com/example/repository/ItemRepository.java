package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	private final NamedParameterJdbcTemplate template;

	private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, i) -> {
		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setDescription(rs.getString("description"));
		item.setPriceM(rs.getInt("price_m"));
		item.setPriceL(rs.getInt("price_l"));
		item.setImagePath(rs.getString("image_path"));
		item.setDeleted(rs.getBoolean("deleted"));
		return item;
	};

	private static final RowMapper<String> NAME_ROW_MAPPER = (rs, i) -> {
		String name = rs.getString("name");
		return name;
	};

	/**
	 * 商品全件検索
	 * 
	 * @return
	 */
	public List<Item> findAll() {
		String findAllSql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE deleted = false ORDER BY price_m;";
		List<Item> itemList = template.query(findAllSql, ITEM_ROW_MAPPER);
		return itemList;
	}

	/**
	 * 商品名から検索
	 * 
	 * @param name
	 * @return
	 */
	public List<Item> findByName(String name) {
		String findByNameSql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE name like :name and deleted = false ORDER BY price_m;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%");
		List<Item> itemList = template.query(findByNameSql, param, ITEM_ROW_MAPPER);
		if (itemList.size() == 0) {
			return itemList;
		}
		return itemList;
	}

	/**
	 * 商品詳細のSQLを発行
	 * 
	 * @param id 商品ID
	 * @return Item情報１件
	 */
	public Optional<Item> showItemDetail(Integer id) {
		String showItemDetailSql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE id = :id;";

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		try {
			Optional<Item> item = Optional
					.ofNullable(template.queryForObject(showItemDetailSql, param, ITEM_ROW_MAPPER));
			return item;
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	/**
	 * 商品IDのリストから該当する商品をまとめて検索する
	 *
	 * @param ids 商品IDのリスト
	 * @return 該当する商品のリスト
	 */
	public List<Item> findByIds(List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		String sql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE id IN (:ids);";
		SqlParameterSource param = new MapSqlParameterSource().addValue("ids", ids);
		return template.query(sql, param, ITEM_ROW_MAPPER);
	}

	public void insert(Item item) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(item);
		String sql = "INSERT INTO items (name, description, price_m, price_l, image_path, deleted)"
				+ " VALUES (:name, :description, :priceM, :priceL, :imagePath, :deleted);";
		template.update(sql, param);
	}

	/**
	 * 商品の名前をすべて返す
	 * 
	 * @return すべてのitemの名前
	 */
	public List<String> getAllNames() {
		String sql = "SELECT name from items WHERE deleted = false;";
		List<String> allNames = template.query(sql, NAME_ROW_MAPPER);
		return allNames;
	}

	public Optional<Item> ApiShowItemDetail(Integer id) {
		String sql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE id = :id;";
		SqlParameterSource param = new MapSqlParameterSource("id", id);
		List<Item> items = template.query(sql, param, ITEM_ROW_MAPPER);

		if (items.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(items.get(0));
		}
	}
}
