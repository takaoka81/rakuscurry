package com.example.service;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Order;
import com.example.domain.StampHistory;
import com.example.domain.User;
import com.example.enums.PayJudge;
import com.example.event.OrderRegisterEvent;
import com.example.repository.OrderRepository;
import com.example.service.Processor.PaymentProcessor;

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

	private final HttpSession session;

	private final OrderRepository orderRepository;

	private final ApplicationEventPublisher publisher;

	private final Map<PayJudge, PaymentProcessor> paymentProcessorMap;

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
		publisher.publishEvent(new OrderRegisterEvent(order, user, stampHistory));
	}

	/**
	 * statusを判別するメゾット
	 *
	 * @param order
	 * @return statusを整数で返す
	 */

	public Integer paymentMethodJudge(Order order) {
		PayJudge payJudge = PayJudge.fromCode(order.getPaymentMethod());

		PaymentProcessor processor = paymentProcessorMap.get(payJudge);
		if (processor == null) {
			throw new IllegalArgumentException("対応する決済方法が見つかりません");
		}
		return processor.resolveStatus(order).getCode();
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
