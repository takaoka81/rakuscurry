package com.example.controller;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.common.SessionCart;
import com.example.domain.LoginUserDetails;
import com.example.domain.Order;
import com.example.domain.StampHistory;
import com.example.domain.User;
import com.example.form.OrderForm;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.example.service.StampService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * 注文確認画面に遷移するためのコントローラー
 * 
 * @author MatsunagaDai,MiyazawaNami
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@ModelAttribute
	public OrderForm setOrderForm() {
		return new OrderForm();
	}

	private final OrderService service;

	private final CartService cartService;

	private final StampService stampService;

	private final HttpSession session;

	private final SessionCart sessionCart;

	@RequestMapping("/toOrder")
	public String toOrder(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Model model) { // Modelを追加

		// 1. セッションからユーザー情報を取得
		User user = loginUserDetails.getUser();

		// 2. ログインチェック
		if (user == null) {
			return "redirect:/toLogin";
		}

		// 3. ★重要：DBからログインユーザーの「未注文(status=0)」の注文情報を取得
		// これにより、さっき保存した商品やトッピング、合計金額がすべて手に入ります
		Order order = cartService.getCartByUserId(user.getId());

		if (order == null || order.getOrderItemList().isEmpty()) {
			// カートが空の場合は、エラーにならないよう適切に処理（またはカート画面へ戻す）
			return "redirect:/cart/showCart";
		}

		// 4. HTML側で計算に使うためのデータをModelにセット
		model.addAttribute("order", order);

		// もしHTML側が ${session.totalPrice} を直接参照している場合は、同期をとるためにセット
		session.setAttribute("totalPrice", order.getTotalPrice());

		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		session.setAttribute("token", token);
		model.addAttribute("token", token);
		return "order/order_confirm";
	}

	/**
	 * 注文完了画面に遷移
	 * 
	 * @param form
	 * @return 完了画面
	 */
	@RequestMapping("/order")
	public String orderCompletion(@AuthenticationPrincipal LoginUserDetails loginUserDetails, @Validated OrderForm form,
			BindingResult result, Model model, @RequestParam("token") String token) {
		// トークンが正しいか
		if (!token.equals((String) session.getAttribute("token"))) {
			return "redirect:/showList";
		}
		session.removeAttribute("token");

		// 昨日の日付を取得し配達日と比較
		Date date = new Date();
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(date);
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		date = yesterday.getTime();

		if (result.hasErrors()) {
			// illigalArgumentExsepsionで捕まるのでif文の中に記載
			if (form.getOrderDate() == null) {
				model.addAttribute("errorDeliveryDate", "配達日を入力してください");
				return "/order/order_confirm";
			}
			if (date.after(form.getOrderDate())) {
				model.addAttribute("errorDeliveryDate", "配達日が過去の日付になっています");
				return "/order/order_confirm";
			}

			return "/order/order_confirm";
		}

		if (date.after(form.getOrderDate())) {
			model.addAttribute("errorDeliveryDate", "配達日が過去の日付になっています");
			return "/order/order_confirm";
		}

		// 配達日時（配達日 + 配達時刻）を組み立てて3時間後判定に使う
		LocalDateTime deliveryDateTime = LocalDateTime.of(
				form.getOrderDate().toLocalDate(),
				LocalTime.of(form.getIntegerDeliveryTime(), 0));
		Timestamp deliveryTime = Timestamp.valueOf(deliveryDateTime);

		LocalDateTime checkDateTime = LocalDateTime.now().plusHours(3);

		if (checkDateTime.isAfter(deliveryDateTime)) {
			model.addAttribute("errorDeliveryDate", "今から3時間後の日時をご入力ください");
			return "/order/order_confirm";
		}

		User user = loginUserDetails.getUser();

		Order order = cartService.getCartByUserId(user.getId());

		if (order == null) {
			return "redirect:/toOrder"; // 万が一カートが取れなかった場合
		}

		BeanUtils.copyProperties(form, order);

		// 郵便番号のハイフンを消してドメインにセット
		order.setDestinationZipcode(form.getDestinationZipcode().replace("-", ""));

		order.setDeliveryTime(deliveryTime);

		Integer chengesStamps = stampService.getStampCountByOrder(order.getOrderItemList());
		Integer addStamps = chengesStamps;
		// 無料適用している場合にプラス分だけを取得する
		if (addStamps < 0) {
			addStamps += 25;
		}
		user = user.toBuilder()
				.stampNowCount(user.getStampNowCount() + chengesStamps)
				.stampAllCount(user.getStampAllCount() + addStamps)
				.build();
		StampHistory stampHistory = new StampHistory(user.getId(), order.getId(), chengesStamps);
		service.order(order, user, stampHistory);

		sessionCart.clear();

		session.removeAttribute("totalPrice");

		return "redirect:/orderCompletion";
	}

	@RequestMapping("orderCompletion")
	public String orderCompletion(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Model model) {
		if (loginUserDetails != null) {
			User user = loginUserDetails.getUser();
			if (user.getStampNowCount() >= 25) {
				Integer freeCurryCount = stampService.getFreeCurryCount(user.getStampNowCount());
				model.addAttribute("freeCurryCount", freeCurryCount);
			}
		}

		return "/order/order_finished";
	}

	/**
	 * 注文履歴のページを表示
	 * 
	 * @param model リクエストスコープ
	 * @return 注文履歴
	 */
	@RequestMapping("/orderHistory")
	public String orderHistory(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Model model) {

		// ユーザーの情報を拾ってくる
		User user = loginUserDetails.getUser();
		// もしログインしていなければログインに戻す
		if (user == null) {
			return "forward:/toLogin";
		}

		List<Order> orderList = service.findByOrder(user.getId());
		if (orderList == null) {
			model.addAttribute("orderNothing", "注文履歴がありません");
		} else {
			model.addAttribute("orderList", orderList);
		}
		logger.info("orderList={}", orderList);

		return "order/order_history";
	}

	@RequestMapping("orderdetail")
	public String orderDetail(Integer id, Model model) {
		logger.info("id={}", id);
		List<Order> orderList = service.orderLoad(id);
		model.addAttribute("orderList", orderList);
		logger.info("orderList={}", orderList);
		return "/order/order_detail";
	}
}
