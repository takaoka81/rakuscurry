package com.example.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.OrderToppingRepository;

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

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderToppingRepository orderToppingRepository;

	@Autowired
	private HttpSession session;

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
	public void order(Order order) {
		order.setStatus(paymentMethodJudge(order));
		order.setUserId(getUserId());
		Integer orderId = orderRepository.insert(order);
		insertOrderItem(orderId);

		// orderオブジェクトに商品情報をセットしておく（メール送信などで必要）
		List<Order> loaded = orderRepository.orderLoad(orderId);
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

	public Integer shoppingMethodJude(Order order){
		if(order.getStatus() == 0){
			return 0;
		}else{
			return 1;
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
	 * order_itemsテーブルにINSERTするメゾット
	 * 
	 * @param orderId
	 */
	private void insertOrderItem(Integer orderId) {
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");

		for (CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			// カートの時点で保持している商品金額をそのまま記録
			BeanUtils.copyProperties(cartItem, orderItem);
			orderItem.setOrderPrice(cartItem.getItemPrice());

			orderItem.setOrderId(orderId);
			Integer orderItemid = orderItemRepository.order(orderItem);

			// サイズ情報も渡してトッピング価格を決定
			InsertOrdertopping(orderItemid, cartItem.getToppingList(), cartItem.getSize());
		}
	}

	/**
	 * order_toppingsテーブルにセット
	 * 
	 * @param orderItemId 注文商品の主キー
	 * @param toppingList 注文商品が持っているtoppingList
	 */
	private void InsertOrdertopping(Integer orderItemId, List<Topping> toppingList, String size) {
		for (Topping topping : toppingList) {
			OrderTopping orderTopping = new OrderTopping();
			orderTopping.setOrderItemId(orderItemId);
			orderTopping.setToppingId(topping.getId());
			// サイズに応じた価格を記録
			if ("M".equals(size)) {
				orderTopping.setOrderPrice(topping.getPriceM());
			} else {
				orderTopping.setOrderPrice(topping.getPriceL());
			}
			orderToppingRepository.insert(orderTopping);
		}
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
							.append(" - 小計: ").append(String.format("%,d", item.getSubTotal())).append("円\n");
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
			e.printStackTrace();
		}
	}
}
