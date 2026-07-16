package com.example.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

import lombok.RequiredArgsConstructor;

/**
 * カート内に商品を入れる際に使うservice
 * 
 * @author naramasato
 *
 */
@Service
@RequiredArgsConstructor
public class CartService {

	private static final Logger logger = LoggerFactory.getLogger(CartService.class);

	private final OrderRepository orderRepository;

	private final OrderItemRepository orderItemRepository;

	private final OrderToppingRepository orderToppingRepository;

	private final UserRepository userRepository;

	private final StampService stampService;

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
		Integer orderId = findOrCreatedId(userId);

		// 2. 子(OrderItem)の登録
		Integer orderItemId = insertOrderItem(cartItem, orderId);

		// 3. 孫(OrderTopping)の登録
		insertOrderTopping(cartItem.getToppingList(), orderItemId, cartItem.getSize());

		// 4. 合計金額の再計算と更新
		updateTotalPriceAfterAdd(orderId, userId);
	}

	/**
	 * カート(Order)があるか確認
	 * 
	 * @param userId
	 * @return カートがあればカートID無ければ新規カートを作成したIDを返す
	 */
	private Integer findOrCreatedId(Integer userId) {
		Optional<Order> order = orderRepository.findByUserIdAndStatus(userId, 0);
		Integer orderId = order.map(Order::getId)
				.orElseGet(() -> {
					Order newOrder = new Order();
					newOrder.setUserId(userId);
					newOrder.setStatus(0);
					return orderRepository.insert(newOrder);
				});
		return orderId;
	}

	/**
	 * 子(OrderItem)の登録
	 * 
	 * @param cartItem
	 * @param orderId
	 * @return OrderItemのID
	 */
	private Integer insertOrderItem(CartItem cartItem, Integer orderId) {
		OrderItem orderItem = new OrderItem();
		BeanUtils.copyProperties(cartItem, orderItem);
		orderItem.setOrderId(orderId);
		orderItem.setOrderPrice(cartItem.getItemPrice());
		return orderItemRepository.order(orderItem);
	}

	/**
	 * 孫(OrderTopping)の登録
	 *
	 * @param toppingList
	 * @param orderItemId
	 * @param size
	 */
	private void insertOrderTopping(List<Topping> toppingList, Integer orderItemId, String size) {
		if (toppingList == null || toppingList.isEmpty()) {
			return;
		}
		List<OrderTopping> orderToppings = toppingList.stream()
				.map(topping -> {
					OrderTopping ot = new OrderTopping();
					ot.setOrderItemId(orderItemId);
					ot.setToppingId(topping.getId());
					ot.setOrderPrice("M".equals(size)
							? topping.getPriceM()
							: topping.getPriceL());
					return ot;
				})
				.toList();
		orderToppingRepository.insert(orderToppings);
	}

	/**
	 * 合計金額を更新する
	 * 
	 * @param orderId
	 * @param userId
	 */
	private void updateTotalPriceAfterAdd(Integer orderId, Integer userId) {
		Optional<Order> updatedOrder = orderRepository.findByUserIdAndStatus(userId, 0);

		// 取得したデータ（updatedOrder）が存在すること、商品リストがあることを確認
		if (updatedOrder.isPresent() && updatedOrder.get().getOrderItemList() != null) {
			int newTotal = 0;
			for (OrderItem item : updatedOrder.get().getOrderItemList()) {
				// 商品ごとの(価格+トッピング価格) * 数量 を加算
				newTotal += item.getSubTotal();
			}
			// ここでDBの orders テーブルの total_price カラムを書き換える
			orderRepository.updateTotalPrice(orderId, newTotal);

			logger.info("DBの合計金額を更新しました: " + newTotal + "円");
		}
	}

	/**
	 * ユーザーIDから未注文のカート情報を取得する
	 */
	@Transactional
	public Order getCartByUserId(Integer userId) {
		// status=0 (カート内) のもgetCartByUserIdのを探す
		return orderRepository.findByUserIdAndStatus(userId, 0)
				.map(order -> adaptFreeCurry(order, userId))
				.orElse(null);

	}

	@Transactional
	private Order adaptFreeCurry(Order order, Integer userId) {
		Integer freeCount = getFreeCount(userId);

		if (freeCount >= 1) {
			List<OrderItem> orderItems = applyFreeDiscount(order.getOrderItemList(), freeCount);

			// 0円適用後に合計金額を反映させる
			int priceResult = calcTotalWithDiscount(orderItems);
			int totalToppingPrice = calcToppingTotal(orderItems);
			int totalPrice = priceResult + totalToppingPrice;

			order.setOrderItemList(orderItems);
			order.setTotalPrice(totalPrice);
			orderRepository.updateTotalPrice(userId, order.getTotalPrice());
		}

		return order;
	}

	/**
	 * 無料枠数の取得
	 * 
	 * @param userId
	 * @return 無料枠数
	 */
	Integer getFreeCount(Integer userId) {
		Integer stampNowCount = userRepository.findByUserId(userId)
				.map(User::getStampNowCount)
				.orElse(0);
		return stampService.getFreeCurryCount(stampNowCount);
	}

	/**
	 * 一番高い商品を無料にする
	 * 
	 * @param orderItems
	 * @param freeCount
	 * @return
	 *
	 */
	List<OrderItem> applyFreeDiscount(List<OrderItem> orderItems, Integer freeCount) {
		orderItems.sort((first, second) -> Integer.compare(second.getOrderPrice(), first.getOrderPrice()));
		List<OrderItem> orderItemsQuantitySingle = createOrderItemsQuantitySingle(orderItems);

		for (int i = 0; i < orderItemsQuantitySingle.size() && i < freeCount; i++) {
			orderItemsQuantitySingle.get(i).setOrderPrice(0);
			orderItemsQuantitySingle.get(i).setFree(true);
		}

		// 同じitemIdのものは一行で表示するために数量を元に戻す
		orderItems = createSameOrderIdListes(orderItemsQuantitySingle);

		// id順に並べ替える
		orderItems.sort(Comparator.comparing(OrderItem::getId));
		return orderItems;
	}

	/**
	 * 合計金額を計算する
	 *
	 * @param orderItems
	 * @return
	 */
	Integer calcTotalWithDiscount(List<OrderItem> orderItems) {
		Integer totalPrice = 0;
		Integer totalSubPrice = 0;
		for (OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getOrderPrice() * orderItem.getQuantity();
			Integer subPrice = orderItem.getOrderPrice() * orderItem.getFreeCount();
			orderItem.setDiscount(subPrice);
			totalSubPrice += subPrice;
		}
		orderItemRepository.update(orderItems);
		return totalPrice - totalSubPrice;
	}

	/**
	 * トッピングの合計金額を計算する
	 * 
	 * @param orderItems
	 * @return
	 */
	Integer calcToppingTotal(List<OrderItem> orderItems) {
		Integer totalToppingPrice = 0;
		for (OrderItem orderItem : orderItems) {
			Integer toppingPrice = 0;
			for (OrderTopping orderTopping : orderItem.getOrderTopping()) {
				toppingPrice += orderTopping.getOrderPrice();
			}
			totalToppingPrice += toppingPrice * orderItem.getQuantity();
		}
		return totalToppingPrice;
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
		orderRepository.findByUserIdAndStatus(userId, 0).ifPresent(order -> {
			int latestTotal = 0;
			if (order.getOrderItemList() != null) {
				for (OrderItem item : order.getOrderItemList()) {
					latestTotal += item.getSubTotal();
				}
			}
			orderRepository.updateTotalPrice(order.getId(), latestTotal);
			order.setTotalPrice(latestTotal);
		});

	}
}
