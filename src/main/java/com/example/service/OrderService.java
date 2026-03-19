package com.example.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.example.controller.LoginController;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.StampHistory;
import com.example.domain.User;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

/**
 * orderに関わる内容を行う
 * 
 * @author naramasato
 *
 */
@Service
@Transactional
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private StampHistoryService stampHistoryService;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private HttpSession session;

	@Autowired
	private OrderRepository orderRepository;

	@Value("${spring.mail.from}")
	private String mailFrom;

	@Value("${spring.mail.subject}")
	private String mailSubject;

	@Autowired
	private MailSender sender;

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
	public void order(Order order, User user, StampHistory stampHistory) {
		order.setStatus(paymentMethodJudge(order));
		orderRepository.update(order);
		userService.updateStampCounts(user);
		stampHistoryService.insert(stampHistory);
		for (OrderItem orderItem : order.getOrderItemList()) {
			if (orderItem.getOrderPrice() == 0) {
				orderItem.setOrderId(order.getId());
				orderItemRepository.updateOrder(orderItem);
			}
		}

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
		if (order.getPaymentMethod() == 1) {
			return 1;
		} else {
			return 2;
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

	/**
	 * 引数で受け取ったemailに完了メールを送付
	 * 
	 * @param order 注文情報
	 */
	public void sendMail(Order order, String email) {
		try {
			// テンプレート読み込み
			Resource resource = new ClassPathResource("templates/mail/order_completion.txt");
			String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

			// プレースホルダー置換
			String customerName = order.getDestinationName();
			String orderId = String.valueOf(order.getId());
			String orderDate = order.getDeliveryTime().toString();
			String destinationName = order.getDestinationName();
			String destinationAddress = order.getDestinationAddress();
			String destinationTel = order.getDestinationTel();
			String totalPrice = String.format("%,d", order.getTotalPrice());
			String paymentMethod = order.getPaymentMethod() == 1 ? "代金引換" : "クレジットカード";

			// 注文商品情報の構築
			StringBuilder orderItems = new StringBuilder();
			List<OrderItem> items = order.getOrderItemList();
			if (items == null) {
				// まだセットされていない場合はDBから読み直す
				List<Order> loaded = orderRepository.orderLoad(order.getId());
				if (loaded != null && !loaded.isEmpty()) {
					items = loaded.get(0).getOrderItemList();
				}
			}
			if (items != null) {
				for (OrderItem item : items) {
					orderItems.append(item.getItem().getName())
							.append(" (").append(item.getSize()).append(") x").append(item.getQuantity())
							.append(" - 小計: ").append(String.format("%,d", item.getSubTotal())).append("円\n\n");
					// トッピング情報
					if (item.getOrderTopping() != null && !item.getOrderTopping().isEmpty()) {
						orderItems.append("  トッピング: ");
						for (OrderTopping topping : item.getOrderTopping()) {
							orderItems.append(topping.getTopping().getName()).append(", ");
						}
						orderItems.setLength(orderItems.length() - 2); // 最後のカンマを削除
						orderItems.append("\n\n");
					}
				}
			}

			// 置換
			String body = template.replace("${customerName}", customerName)
					.replace("${orderId}", orderId)
					.replace("${orderDate}", orderDate)
					.replace("${destinationName}", destinationName)
					.replace("${destinationAddress}", destinationAddress)
					.replace("${destinationTel}", destinationTel)
					.replace("${orderItems}", orderItems.toString().trim())
					.replace("${totalPrice}", totalPrice)
					.replace("${paymentMethod}", paymentMethod);

			// メール送信
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(mailFrom);
			msg.setTo(email);
			msg.setSubject(mailSubject);
			msg.setText(body);

			this.sender.send(msg);
		} catch (IOException e) {
			// ログ出力やエラーハンドリング
			logger.error("処理中にエラーが発生しました", e);
		}
	}
}
