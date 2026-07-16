package com.example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.StampHistory;
import com.example.domain.User;
import com.example.enums.PayJuduge;
import com.example.enums.Status;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * orderに関わる内容を行う
 * 
 * @author naramasato
 *
 */
@Service
@RequiredArgsConstructor
public class OrderService {

	private final UserService userService;

	private final StampHistoryService stampHistoryService;

	private final OrderItemRepository orderItemRepository;

	private final HttpSession session;

	private final OrderRepository orderRepository;

	/**
	 * 注文詳細一件を取得
	 * 
	 * @param orderId
	 * @return
	 */
	public List<Order> orderLoad(Integer orderId) {
		return orderRepository.orderLoad(orderId);
	}

	/**
	 * 注文詳細全件を取得
	 * 
	 * @param id
	 * @return
	 */
	public List<Order> findByOrder(Integer id) {
		return orderRepository.findByOrdertable(id);
	}

	/**
	 * orderドメインに足りない物をセット
	 * 
	 * @param order
	 */
	@Transactional
	public void order(Order order, User user, StampHistory stampHistory) {
		order.setStatus(paymentMethodJudge(order));
		orderRepository.update(order);
		userService.updateStampCounts(user);
		stampHistoryService.insert(stampHistory);
		List<OrderItem> oi = new ArrayList<>();
		for (OrderItem orderItem : order.getOrderItemList()) {
			if (orderItem.getOrderPrice().equals(0)) {
				orderItem.setOrderId(order.getId());
				oi.add(orderItem);
			}
		}
		orderItemRepository.updateOrder(oi);

		// orderオブジェクトに商品情報をセットしておく（メール送信などで必要）
		List<Order> loaded = orderRepository.orderLoad(order.getId());
		if (loaded != null && !loaded.isEmpty()) {
			order.setOrderItemList(loaded.get(0).getOrderItemList());
		}
	}

	/**
	 * statusを判別するメゾット
	 *
	 * @param order
	 * @return statusを整数で返す
	 */
	public Integer paymentMethodJudge(Order order) {
		if (PayJuduge.fromCode(order.getPaymentMethod()) == PayJuduge.COD) {
			return Status.ORDER.getCode();
		} else {
			return Status.PAYMENT_RECEIVED.getCode();
		}
	}

	/**
	 * ユーザーのIdを返すメゾット
	 * 
	 * @return userId
	 */
	public Integer getUserId() {
		User user = (User) session.getAttribute("user");
		return user.getId();
	}

}
