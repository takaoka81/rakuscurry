package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.StreamUtils;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.enums.PayJudge;
import com.example.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

/**
 * メール送信に関わる内容を行う
 *
 */
@RequiredArgsConstructor
public class SimpleMailService implements MailService {
	private static final Logger logger = LoggerFactory.getLogger(SimpleMailService.class);

	private final OrderRepository orderRepository;

	@Value("${spring.mail.from}")
	private String mailFrom;

	@Value("${spring.mail.subject}")
	private String mailSubject;

	private final MailSender sender;

	/**
	 * 引数で受け取ったemailに完了メールを送付
	 *
	 * @param order 注文情報
	 * @param to    宛先（ユーザーの登録されているメールアドレス）
	 */

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
			throw new RuntimeException("メール送信処理中にエラーが発生しました", e);
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
					String toppingNames = item.getOrderTopping().stream()
							.map(topping -> topping.getTopping().getName())
							.collect(Collectors.joining(", "));
					orderItems.append("  トッピング: ").append(toppingNames).append("\n\n");
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
		String paymentMethod = PayJudge.fromCode(order.getPaymentMethod()) == PayJudge.COD ? "代金引換" : "クレジットカード";
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
