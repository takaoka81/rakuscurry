package com.example.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.domain.User;

/**
 * オーダー内容をSQLとやりとりする
 * 
 * @author naramasato
 *
 */
@Repository
public class OrderRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	// orders,users,order_items,items,order_toppings,toppingsのテーブル結合に使うResultSetExtractor
	private static final ResultSetExtractor<List<Order>> ORDER_RESULTSET = (rs) -> {

		// OrderのListに新たに格納
		List<Order> orderList = new ArrayList<>();

		// orderItemのListを作成。外部キーに紐づいてるテーブル毎に発行してしまう為初めはnullで宣言しておく
		List<OrderItem> orderItemList = null;

		// orderToppoingのListを作成。外部キーに紐づいてるテーブル毎に発行してしまう為初めはnullで宣言しておく
		List<OrderTopping> orderTopping = null;

		// 前回のorderのidを入れる箱
		int beforeIdNum = 0;

		// 前回のorderItemのidを入れる箱
		int itemBefore = 0;

		while (rs.next()) {
			// 現在のorderidを代入
			int nowIdNum = rs.getInt("o_id");

			// 現在のidと前回のidが被ってないか確認
			if (nowIdNum != beforeIdNum) {

				// orderオブジェクトにSQLのデータをセット
				Order order = new Order();
				order.setId(nowIdNum);
				order.setUserId(rs.getInt("o_user_id"));
				order.setStatus(rs.getInt("o_status"));
				order.setTotalPrice(rs.getInt("o_total_price"));
				order.setOrderDate(rs.getDate("o_order_date"));
				order.setDestinationName(rs.getString("o_destination_name"));
				order.setDestinationEmail(rs.getString("o_destination_email"));
				order.setDestinationZipcode(rs.getString("o_destination_zipcode"));
				order.setDestinationAddress(rs.getString("o_destination_address"));
				order.setDestinationTel(rs.getString("o_destination_tel"));
				order.setDeliveryTime(rs.getTimestamp("o_delivery_time"));
				order.setPaymentMethod(rs.getInt("o_payment_method"));

				// Userをセット
				User user = new User();
				user.setId(rs.getInt("u_id"));
				user.setName(rs.getString("u_name"));
				user.setPassword(rs.getString("u_password"));
				user.setEmail(rs.getString("u_email"));
				user.setZipcode(rs.getString("u_zipcode"));
				user.setAddress(rs.getString("u_address"));
				user.setTelephone(rs.getString("u_telephone"));

				// Orderドメインが持っているuserフィールドに外部キーで紐づいているUserをセット
				order.setUser(user);

				// 既に宣言してるorderItemListに新規でArrayListをセット
				orderItemList = new ArrayList<OrderItem>();
				order.setOrderItemList(orderItemList);

				orderList.add(order);
			}

			// 現在のorderItemidを代入
			int itemNow = rs.getInt("oi_id");
			// 現在のorderItemidと前回のorderItemidが被ってないか確認
			if (itemNow != itemBefore) {
				// orderItemオブジェクトにSQLのデータをセット
				OrderItem orderItem = new OrderItem();
				orderItem.setId(rs.getInt("oi_id"));
				orderItem.setItemId(rs.getInt("oi_item_id"));
				orderItem.setOrderId(rs.getInt("oi_order_id"));
				orderItem.setQuantity(rs.getInt("oi_quantity"));
				orderItem.setSize(rs.getString("oi_size"));
				orderItem.setOrderPrice(rs.getInt("oi_order_price"));
				orderItem.setDiscount(rs.getInt("oi_discount"));

				// Itemをセット
				Item item = new Item();
				item.setId(rs.getInt("i_id"));
				item.setName(rs.getString("i_name"));
				item.setDescription(rs.getString("i_description"));
				item.setPriceM(rs.getInt("i_price_m"));
				item.setPriceL(rs.getInt("i_price_l"));
				item.setImagePath(rs.getString("i_image_path"));
				item.setDeleted(rs.getBoolean("i_deleted"));

				// OrderItemドメインが持っているItemフィールドに外部キーで紐づいているItemをセット
				orderItem.setItem(item);

				// 既に宣言してるorderToppingListに新規でArrayListをセット
				orderTopping = new ArrayList<OrderTopping>();
				orderItem.setOrderTopping(orderTopping);

				orderItemList.add(orderItem);
			}

			// orderToppingを持っていないorderItemにオブジェクトを作らないようにする
			if (rs.getInt("ot_id") != 0) {
				// orderToppingオブジェクトにSQLのデータをセット
				OrderTopping orderToppings = new OrderTopping();
				orderToppings.setId(rs.getInt("ot_id"));
				orderToppings.setToppingId(rs.getInt("ot_topping_id"));
				orderToppings.setOrderItemId(rs.getInt("ot_order_item_id"));
				orderToppings.setOrderPrice(rs.getInt("ot_order_price"));

				// toppingをセット
				Topping topping = new Topping();
				topping.setId(rs.getInt("t_id"));
				topping.setName(rs.getString("t_name"));
				topping.setPriceM(rs.getInt("t_price_m"));
				topping.setPriceL(rs.getInt("t_price_l"));
				// toppingの情報をorderToppingにセット
				orderToppings.setTopping(topping);
				orderTopping.add(orderToppings);
			}
			// 現在のOrderItemのidを代入
			itemBefore = itemNow;
			// 現在のOrderのidを代入
			beforeIdNum = nowIdNum;
		}
		return orderList;
	};

	/**
	 * orderの詳細を表示するメゾット
	 * 
	 * @param orderId
	 * @return List<Order> １件の注文内容
	 */
	public List<Order> orderLoad(Integer orderId) {
		String sql = "SELECT o.id AS o_id, o.user_id AS o_user_id, o.status AS o_status, o.total_price AS o_total_price, "
				+ "o.order_date AS o_order_date, o.destination_name AS o_destination_name, o.destination_email AS o_destination_email, "
				+ "o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address, o.destination_tel AS o_destination_tel, "
				+ "o.delivery_time AS o_delivery_time, o.payment_method AS o_payment_method, "
				+ "u.id AS u_id, u.name AS u_name, u.password AS u_password, u.email As u_email, u.zipcode AS u_zipcode, u.address AS u_address, u.telephone AS u_telephone, "
				+ "oi.id AS oi_id, oi.item_id AS oi_item_id, oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, "
				+ "oi.size AS oi_size, oi.order_price AS oi_order_price, oi.discount AS oi_discount,"
				+ "i.id AS i_id, i.name AS i_name, i.description AS i_description, i.price_m AS i_price_m, i.price_l AS i_price_l, "
				+ "i.image_path AS i_image_path, i.deleted AS i_deleted, "
				+ "ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id, ot.order_price AS ot_order_price, t.id AS t_id, t.name AS t_name, "
				+ "t.price_m AS t_price_m, t.price_l AS t_price_l "
				+ "FROM orders o "
				+ "JOIN users u ON o.user_id = u.id "
				+ "RIGHT JOIN order_items oi ON o.id = oi.order_id "
				+ "RIGHT JOIN items i ON oi.item_id = i.id "
				+ "LEFT OUTER JOIN order_toppings ot ON oi.id = ot.order_item_id "
				+ "LEFT OUTER JOIN toppings t ON ot.topping_id = t.id "
				+ "WHERE o.id = :orderId AND o.status IN (1, 2) ORDER BY i.id DESC";

		SqlParameterSource param = new MapSqlParameterSource().addValue("orderId", orderId);

		List<Order> orderList = template.query(sql, param, ORDER_RESULTSET);
		return orderList;
	}

	/**
	 * orders,users,order_items,items,order_toppings,toppingsのテーブルを結合して出力
	 * 
	 * @param userId
	 * @return
	 */
	public List<Order> findByOrdertable(Integer userId) {
		String sql = "SELECT o.id AS o_id, o.user_id AS o_user_id, o.status AS o_status, o.total_price AS o_total_price, "
				+ "o.order_date AS o_order_date, o.destination_name AS o_destination_name, o.destination_email AS o_destination_email, "
				+ "o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address, o.destination_tel AS o_destination_tel, "
				+ "o.delivery_time AS o_delivery_time, o.payment_method AS o_payment_method, "
				+ "u.id AS u_id, u.name AS u_name, u.password AS u_password, u.email As u_email, u.zipcode AS u_zipcode, u.address AS u_address, u.telephone AS u_telephone, "
				+ "oi.id AS oi_id, oi.item_id AS oi_item_id, oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, "
				+ "oi.size AS oi_size, oi.order_price AS oi_order_price, oi.discount AS oi_discount,"
				+ "i.id AS i_id, i.name AS i_name, i.description AS i_description, i.price_m AS i_price_m, i.price_l AS i_price_l, "
				+ "i.image_path AS i_image_path, i.deleted AS i_deleted, "
				+ "ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id, ot.order_price AS ot_order_price, t.id AS t_id, t.name AS t_name, "
				+ "t.price_m AS t_price_m, t.price_l AS t_price_l "
				+ "FROM orders o "
				+ "JOIN users u ON o.user_id = u.id "
				+ "RIGHT JOIN order_items oi ON o.id = oi.order_id "
				+ "RIGHT JOIN items i ON oi.item_id = i.id "
				+ "LEFT OUTER JOIN order_toppings ot ON oi.id = ot.order_item_id "
				+ "LEFT OUTER JOIN toppings t ON ot.topping_id = t.id "
				+ "WHERE o.user_id = :userId AND o.status IN (1,2) ORDER BY o.order_date DESC, o.id DESC, i.id DESC";

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);

		List<Order> orderList = template.query(sql, param, ORDER_RESULTSET);

		if (orderList.size() == 0) {
			return orderList;
		}
		return orderList;
	}

	/**
	 * Orderの情報をSQLに登録
	 * 
	 * @param order
	 * @return ordersテーブルに登録された際に発行されたidを返す
	 */
	public Integer insert(Order order) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		String insertSql = "INSERT INTO orders (user_id, status, total_price, order_date, destination_name, destination_email, destination_zipcode, destination_address, destination_tel, delivery_time, payment_method) "
				+ "VALUES (:userId, :status, :totalPrice, :orderDate, :destinationName, :destinationEmail, :destinationZipcode, :destinationAddress, :destinationTel, :deliveryTime, :paymentMethod);";

		// 自動採番の際にidを取得する
		KeyHolder keyHolder = new GeneratedKeyHolder();
		String[] keyColumnNames = { "id" };
		template.update(insertSql, param, keyHolder, keyColumnNames);
		order.setId(keyHolder.getKey().intValue());

		return order.getId();
	}

	/**
	 * 注文情報を更新する（注文確定用）
	 */
	public void update(Order order) {
		String sql = "UPDATE orders SET "
				+ "status = :status, "
				+ "total_price = :totalPrice, "
				+ "order_date = :orderDate, "
				+ "destination_name = :destinationName, "
				+ "destination_email = :destinationEmail, "
				+ "destination_zipcode = :destinationZipcode, "
				+ "destination_address = :destinationAddress, "
				+ "destination_tel = :destinationTel, "
				+ "delivery_time = :deliveryTime, "
				+ "payment_method = :paymentMethod "
				+ "WHERE id = :id"; // orderのidで指定

		SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		template.update(sql, param);
	}

	/**
	 * 特定のユーザーとステータスに紐づく注文情報を1件取得する
	 * 関連するOrderItemとOrderToppingもすべて結合して取得
	 */
	/**
	 * カート情報（注文前データ）を1件取得する
	 */
	public Optional<Order> findByUserIdAndStatus(Integer userId, Integer status) {

		String sql = "SELECT o.id AS o_id, o.user_id AS user_id, o.status AS status, o.total_price AS o_total_price, "
				+ "oi.id AS oi_id, oi.item_id, "
				+ "oi.quantity AS oi_quantity, "
				+ "oi.size AS oi_size, "
				+ "oi.order_price AS oi_order_price, "
				+ "oi.discount AS oi_discount,"
				+ "i.name AS i_name, i.image_path AS i_image_path, "
				+ "ot.id AS ot_id, ot.topping_id AS ot_topping_id, "
				+ "ot.order_price AS ot_order_price, "
				+ "t.name AS t_name, t.price_m AS t_price_m, t.price_l AS t_price_l "
				+ "FROM orders o "
				+ "LEFT OUTER JOIN order_items oi ON o.id = oi.order_id "
				+ "LEFT OUTER JOIN items i ON oi.item_id = i.id "
				+ "LEFT OUTER JOIN order_toppings ot ON oi.id = ot.order_item_id "
				+ "LEFT OUTER JOIN toppings t ON ot.topping_id = t.id "
				+ "WHERE o.user_id = :userId AND o.status = :status";

		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("userId", userId)
				.addValue("status", status);

		return Optional.ofNullable(template.query(sql, param, orderResultSetExtractor));
	}

	/**
	 * 新規カートを作成し、発行されたIDを返す
	 */
	public Integer insertOrder(Integer userId) {
		String sql = "INSERT INTO orders (user_id, status, total_price) "
				+ "VALUES (:userId, :status, total_price) RETURNING id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
		return template.queryForObject(sql, param, Integer.class);
	}

	private final ResultSetExtractor<Order> orderResultSetExtractor = (rs) -> {
		Order order = null;
		Map<Integer, OrderItem> itemMap = new LinkedHashMap<>();

		while (rs.next()) {
			// 1. Orderの作成（ここはそのまま）
			if (order == null) {
				order = new Order();
				order.setId(rs.getInt("o_id"));
				order.setUserId(rs.getInt("user_id"));
				order.setStatus(rs.getInt("status"));
				order.setTotalPrice(rs.getInt("o_total_price"));
				order.setOrderItemList(new ArrayList<>());
			}

			// 2. OrderItemの処理（computeIfAbsentをやめて、ifでチェックする）
			int oiId = rs.getInt("oi_id");
			if (oiId != 0) {
				OrderItem orderItem = itemMap.get(oiId);

				if (orderItem == null) {
					// まだMapにない場合だけ新しく作る
					orderItem = new OrderItem();
					orderItem.setId(oiId);
					orderItem.setItemId(rs.getInt("item_id"));
					orderItem.setOrderPrice(rs.getInt("oi_order_price"));
					orderItem.setQuantity(rs.getInt("oi_quantity"));
					orderItem.setSize(rs.getString("oi_size"));
					orderItem.setDiscount(rs.getInt("oi_discount"));
					orderItem.setOrderTopping(new ArrayList<>());

					Item item = new Item();
					item.setName(rs.getString("i_name"));
					item.setImagePath(rs.getString("i_image_path"));
					orderItem.setItem(item);

					// Mapに保存
					itemMap.put(oiId, orderItem);
					// ★ここなら order が再代入された後でも、ラムダの外なのでエラーになりません！
					order.getOrderItemList().add(orderItem);
				}

				// 3. トッピングの処理
				int otId = rs.getInt("ot_id");
				if (otId != 0) {
					OrderTopping orderTopping = new OrderTopping();
					orderTopping.setId(otId);
					orderTopping.setToppingId(rs.getInt("ot_topping_id"));
					orderTopping.setOrderPrice(rs.getInt("ot_order_price"));

					Topping topping = new Topping();
					topping.setName(rs.getString("t_name"));
					topping.setPriceM(rs.getInt("t_price_m"));
					topping.setPriceL(rs.getInt("t_price_l"));
					orderTopping.setTopping(topping);

					orderItem.getOrderTopping().add(orderTopping);
				}
			}
		}
		return order;
	};

	/**
	 * 指定した注文の合計金額を更新する
	 * 
	 * @param orderId    注文ID
	 * @param totalPrice 計算済みの合計金額
	 */
	public void updateTotalPrice(Integer orderId, Integer totalPrice) {
		String sql = "UPDATE orders SET total_price = :totalPrice WHERE id = :orderId";

		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("totalPrice", totalPrice)
				.addValue("orderId", orderId);

		template.update(sql, param);
	}

	/**
	 * 注文商品を削除する（カスケードによりトッピングも自動削除される）
	 * 
	 * @param orderItemId 削除したいOrderItemのID
	 */
	public void deleteOrderItem(Integer orderItemId) {
		String sql = "DELETE FROM order_items WHERE id = :orderItemId";
		SqlParameterSource param = new MapSqlParameterSource().addValue("orderItemId", orderItemId);
		template.update(sql, param);
	}

}
