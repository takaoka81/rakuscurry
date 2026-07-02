package com.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.form.ItemCartInForm;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.OrderToppingRepository;
import com.example.repository.UserRepository;

/**
 * カート内に商品を入れる際に使うservice
 * 
 * @author naramasato
 *
 */
@Service
@Transactional
public class CartService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderToppingRepository orderToppingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StampService stampService;

	public ItemCartInForm setupForm() {
		return new ItemCartInForm();
	}

	public List<Order> orderLoad(Integer orderId) {
		return orderRepository.orderLoad(orderId);
	}

	/**
	 * 商品をカート(DB)に登録するメインメソッド
	 * * @param cartItem 画面から届いた商品情報
	 * 
	 * @param userId ログインユーザーのID
	 */
	@Transactional
	public void addItemToCart(CartItem cartItem, Integer userId) {

		// 1. カート(Order)があるか確認
		Order order = orderRepository.findByUserIdAndStatus(userId, 0);
		Integer orderId;

		if (order == null) {
			// カートがなければ新規作成
			order = new Order();
			order.setUserId(userId);
			order.setStatus(0);
			order.setTotalPrice(0);
			orderId = orderRepository.insert(order); // ここで1回だけinsert
		} else {
			// 既存のカートがあればそのIDを使う
			orderId = order.getId();
		}

		// ★修正ポイント：ここの余分な orderRepository.insert(order) は削除してください

		// 2. 子(OrderItem)の登録
		OrderItem orderItem = new OrderItem();
		BeanUtils.copyProperties(cartItem, orderItem);
		orderItem.setOrderId(orderId); // 確定したorderIdをセット
		orderItem.setOrderPrice(cartItem.getItemPrice());

		// リポジトリのメソッド名が order() になっている場合はそのままでOK
		Integer orderItemId = orderItemRepository.order(orderItem);

		// 3. 孫(OrderTopping)の登録
		List<Topping> toppingList = cartItem.getToppingList();
		if (toppingList != null && !toppingList.isEmpty()) {
			for (Topping topping : toppingList) {
				OrderTopping ot = new OrderTopping();
				ot.setOrderItemId(orderItemId);
				ot.setToppingId(topping.getId());

				if ("M".equals(cartItem.getSize())) {
					ot.setOrderPrice(topping.getPriceM());
				} else {
					ot.setOrderPrice(topping.getPriceL());
				}
				orderToppingRepository.insert(ot);
			}
		}

		// 4. 合計金額の再計算と更新
		// これを行うことで、DBの total_price が null や 0 でなくなるため、HTMLでの掛け算エラーが消えます
		Order updatedOrder = orderRepository.findByUserIdAndStatus(userId, 0);

		// 取得したデータ（updatedOrder）がnullでないこと、商品リストがあることを確認
		if (updatedOrder != null && updatedOrder.getOrderItemList() != null) {
			int newTotal = 0;
			for (OrderItem item : updatedOrder.getOrderItemList()) {
				// 商品ごとの(価格+トッピング価格) * 数量 を加算
				newTotal += item.getSubTotal();
			}
			// ここでDBの orders テーブルの total_price カラムを書き換える
			orderRepository.updateTotalPrice(orderId, newTotal);

			System.out.println("DBの合計金額を更新しました: " + newTotal + "円");
		}
	}

	/**
	 * ユーザーIDから未注文のカート情報を取得する
	 */
	public Order getCartByUserId(Integer userId) {
		// status=0 (カート内) のもgetCartByUserIdのを探す
		Order order = orderRepository.findByUserIdAndStatus(userId, 0);
		return adaptFreeCurry(order, userId);
	}

	private Order adaptFreeCurry(Order order, Integer userId) {
		if (order == null) {
			return null;
		}

		Integer stampNowCount = userRepository.findByUserId(userId)
				.map(User::getStampNowCount)
				.orElse(0);
		Integer freeCount = stampService.getFreeCurryCount(stampNowCount);

		if (freeCount >= 1) {
			List<OrderItem> orderItems = order.getOrderItemList();

			// 金額が高い順に並べ替え変える
			orderItems.sort((first, second) -> Integer.compare(second.getOrderPrice(), first.getOrderPrice()));

			// 数量1に置き変えたリストを作成
			List<OrderItem> orderItemsQuantitySingle = createOrderItemsQuantitySingle(orderItems);

			// 値段が高い順に無料適用数に応じて0円にする
			for (int i = 0; i < orderItemsQuantitySingle.size() && i < freeCount; i++) {
				orderItemsQuantitySingle.get(i).setOrderPrice(0);
				orderItemsQuantitySingle.get(i).setFree(true);
			}

			// 同じitemIdのものは一行で表示するために数量を元に戻す
			orderItems = createSameOrderIdListes(orderItemsQuantitySingle);

			// id順に並べ替える
			orderItems.sort(Comparator.comparing(OrderItem::getId));

			// 0円適用後に合計金額を反映させる
			Integer totalPrice = 0;
			Integer totalSubPrice = 0;
			for (OrderItem orderItem : orderItems) {
				Integer subPrice = 0;
				totalPrice += orderItem.getOrderPrice() * orderItem.getQuantity();
				subPrice = orderItem.getOrderPrice() * orderItem.getFreeCount();
				orderItem.setDiscount(subPrice);
				totalSubPrice += subPrice;
				orderItemRepository.update(orderItem);
			}

			Integer totalToppingPrice = 0;
			for (OrderItem orderItem : orderItems) {
				Integer toppingPrice = 0;
				for (OrderTopping orderTopping : orderItem.getOrderTopping()) {
					toppingPrice += orderTopping.getOrderPrice();
				}
				totalToppingPrice += toppingPrice * orderItem.getQuantity();
			}

			order.setOrderItemList(orderItems);
			order.setTotalPrice(totalPrice - totalSubPrice + totalToppingPrice);
			orderRepository.updateTotalPrice(userId, order.getTotalPrice());
		}

		return order;
	}

	private List<OrderItem> createOrderItemsQuantitySingle(List<OrderItem> orderItems) {
		List<OrderItem> orderItemsQuantitySingle = new ArrayList<>();
		for (OrderItem orderItem : orderItems) {
			if (orderItem.getQuantity() >= 2) {
				// orderItem.setDiscount(orderItem.getOrderPrice() * orderItem.getQuantity());
				for (int i = 1; i <= orderItem.getQuantity(); i++) {
					OrderItem item = new OrderItem();
					item.setId(orderItem.getId());
					item.setItemId(orderItem.getItemId());
					// item.setOrderId(orderItem.getOrderId());
					item.setQuantity(1);
					item.setSize(orderItem.getSize());
					item.setOrderPrice(orderItem.getOrderPrice());
					item.setItem(orderItem.getItem());
					item.setOrderTopping(orderItem.getOrderTopping());
					orderItemsQuantitySingle.add(item);
				}
			} else {
				orderItemsQuantitySingle.add(orderItem);
			}
		}
		return orderItemsQuantitySingle;
	}

	private List<OrderItem> createSameOrderIdListes(List<OrderItem> orderItems) {
		Map<String, OrderItem> orderItemSameIds = new LinkedHashMap<>();
		for (OrderItem item : orderItems) {
			String key = item.getItemId() + "_" + item.getSize();
			if (orderItemSameIds.containsKey(key)) {
				OrderItem existing = orderItemSameIds.get(key);
				existing.setOrderPrice(item.getOrderPrice());
				existing.setQuantity(existing.getQuantity() + item.getQuantity());
				// freeCount を増やす
				if (item.isFree()) {
					existing.setFreeCount(existing.getFreeCount() + 1);
				}
				// isFree の集約（1つでも true があれば true）
				if (item.isFree()) {
					existing.setFree(true);
				}
			} else {
				orderItemSameIds.put(key, item);
				// freeCount 初期化
				item.setFreeCount(item.isFree() ? 1 : 0);
			}
		}
		return new ArrayList<>(orderItemSameIds.values());
	}

	/**
	 * サイズに合わせてitemの金額を判定
	 * 
	 * @param form
	 * @return itemの金額
	 */
	public Integer getPriceSize(ItemCartInForm form) {
		if (form.getSize().equals("M")) {
			return form.getPriceM();
		} else {
			return form.getPriceL();
		}
	}

	/**
	 * 
	 * @param toppingList  applicationスコープ内に格納してあるトッピング一覧
	 * @param toppingIndex formから送られてきたトッピングの情報
	 * @return トッピング一覧を格納したList 一覧がなければLinkedListで返す
	 * 
	 */
	public List<Topping> getToppingIndex(List<Topping> toppingList, List<String> toppingIndex) {
		if (toppingIndex == null) {
			return new LinkedList<>();
		}
		List<Topping> toppings = new LinkedList<>();
		for (String index : toppingIndex) {
			Topping topping = toppingList.get(Integer.parseInt(index));
			toppings.add(topping);
		}
		return toppings;
	}

	// 合計金額を計算するメソッド
	public Integer calcTotal(List<CartItem> totalPriceList) {

		Integer totalPrices = 0;
		for (CartItem price : totalPriceList) {
			totalPrices += price.getSubTotal();
		}
		return totalPrices;
	}

	/**
	 * カート内の商品を削除し、合計金額を再更新する
	 * 
	 * @param orderItemId 削除する商品のID
	 */
	@Transactional
	public void deleteOrderItem(Integer orderItemId, Integer userId) {
		// 1. 商品を削除
		orderRepository.deleteOrderItem(orderItemId);

		// 2. orderRepository に既にある「最新のOrderを取得するメソッド」を呼びます
		Order order = orderRepository.findByUserIdAndStatus(userId, 0);

		if (order != null) {
			// 3. ★ここで「再計算」を強制的に行う
			int latestTotal = 0;
			if (order.getOrderItemList() != null) {
				for (OrderItem item : order.getOrderItemList()) {
					// 各商品の小計を足し合わせる
					latestTotal += item.getSubTotal();
				}
			}

			// 4. ★計算した「最新の金額」でDBを更新する
			orderRepository.updateTotalPrice(order.getId(), latestTotal);

			// 5. Controllerで使うために、オブジェクト内の金額も更新しておく
			order.setTotalPrice(latestTotal);
		}
	}
}
