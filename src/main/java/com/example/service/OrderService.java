package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
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
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final UserService userService;

	private final StampHistoryService stampHistoryService;

	private final OrderItemRepository orderItemRepository;

	private final HttpSession session;

	private final OrderRepository orderRepository;

	@Value("${spring.mail.from}")
	private String mailFrom;

	@Value("${spring.mail.subject}")
	private String mailSubject;

	private final MailSender sender;

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

	/**
	 * 引数で受け取ったemailに完了メールを送付
	 * 
	 * @param order 注文情報
	 * @param to    宛先（ユーザーの登録されているメールアドレス）
	 */
	@Transactional
	public void sendMail(Order order, String to) {
		try {
			// テンプレート読み込み
			String template = loadMailTemplate();

			// 注文商品情報の構築
			String orderItems = buildOrderItems(order);

			// 置換
			String body = buildMailBody(template, order, orderItems);

			// メール送信
			sendMailMessage(to, body);

		} catch (IOException e) {
			// ログ出力やエラーハンドリング
			logger.error("処理中にエラーが発生しました", e);
		}
	}

	/**
	 * テンプレート読み込み
	 *
	 * @return
	 * @throws IOException
	 */
	private String loadMailTemplate() throws IOException {
		Resource resource = new ClassPathResource("templates/mail/order_completion.txt");
		try (InputStream inputStream = resource.getInputStream()) {
			return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
		}
	}

	/**
	 * 注文商品情報のメール構築
	 * 商品1つずつの小計とトッピングを見やすく表示させる
	 * 
	 * @param order
	 * @return
	 */
	private String buildOrderItems(Order order) {
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
		return orderItems.toString().trim();
	}

	/**
	 * メール本文の構築
	 *
	 * @param template       メールのテンプレート文
	 * @param order          メールのテンプレートに置換する内容
	 * @param orderItemsText メールに入力する内容
	 * @return
	 */
	private String buildMailBody(String template, Order order, String orderItemsText) {
		String paymentMethod = PayJuduge.fromCode(order.getPaymentMethod()) == PayJuduge.COD ? "代金引換" : "クレジットカード";
		return template.replace("${customerName}", order.getDestinationName())
				.replace("${orderId}", String.valueOf(order.getId()))
				.replace("${orderDate}", order.getDeliveryTime().toString())
				.replace("${destinationName}", order.getDestinationName())
				.replace("${destinationAddress}", order.getDestinationAddress())
				.replace("${destinationTel}", order.getDestinationTel())
				.replace("${orderItems}", orderItemsText)
				.replace("${totalPrice}", String.format("%,d", order.getTotalPrice()))
				.replace("${paymentMethod}", paymentMethod);
	}

	/**
	 * メール送信
	 * 
	 * @param to   宛先
	 * @param body 内容
	 */
	private void sendMailMessage(String to, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(mailFrom);
		msg.setTo(to);
		msg.setSubject(mailSubject);
		msg.setText(body);
		this.sender.send(msg);
	}
}
