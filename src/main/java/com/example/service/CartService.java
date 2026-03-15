package com.example.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.form.ItemCartInForm;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.OrderToppingRepository;

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
    
    int newTotal = 0;
    // 取得したデータ（updatedOrder）がnullでないこと、商品リストがあることを確認
    if (updatedOrder != null && updatedOrder.getOrderItemList() != null) {
        for (OrderItem item : updatedOrder.getOrderItemList()) {
            // 商品ごとの(価格+トッピング価格) * 数量 を加算
            newTotal += item.getSubTotal(); 
        }
        // ここでDBの orders テーブルの total_price カラムを書き換える
        orderRepository.updateTotalPrice(orderId, newTotal);
    }
}
	

	/**
	 * ユーザーIDから未注文のカート情報を取得する
	 */
	public Order getCartByUserId(Integer userId) {
		// status=0 (カート内) のものを探す
		return orderRepository.findByUserIdAndStatus(userId, 0);
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
 * @param orderItemId 削除する商品のID
 */
/* @Transactional
public void deleteOrderItem(Integer orderItemId) {
    // 1. 削除前に、どの注文(orderId)に紐付いているか確認しておく
    OrderItem item = orderItemRepository.load(orderItemId);
    Integer orderId = item.getOrderId();

    // 2. トッピングと商品を順番に削除
    orderToppingRepository.deleteByOrderItemId(orderItemId);
    orderItemRepository.deleteById(orderItemId);

    // 3. 削除後の最新状態で合計金額を再計算してUPDATE（重要）
    Order updatedOrder = orderRepository.load(orderId);
    int newTotal = 0;
    for (OrderItem orderItem : updatedOrder.getOrderItemList()) {
        newTotal += orderItem.getSubTotal();
    }
    orderRepository.updateTotalPrice(orderId, newTotal);
} */
}
