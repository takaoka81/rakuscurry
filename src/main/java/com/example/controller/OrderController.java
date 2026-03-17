package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Order;
import com.example.domain.User;
import com.example.form.OrderForm;
import com.example.service.CartService;
import com.example.service.OrderService;

import jakarta.servlet.http.HttpSession;

/**
 * 注文確認画面に遷移するためのコントローラー
 * 
 * @author MatsunagaDai,MiyazawaNami
 *
 */
@Controller
@RequestMapping("")
public class OrderController {

	@ModelAttribute
	public OrderForm setOrderForm() {
		return new OrderForm();
	}

	@Autowired
	private OrderService service;

	@Autowired
	private CartService cartService;

	@Autowired
	private HttpSession session;

	public OrderForm setUpOrderForm() {
		return new OrderForm();
	}

	@RequestMapping("/toOrder")
	public String toOrder(Model model) { // Modelを追加

		// 1. セッションからユーザー情報を取得
		User user = (User) session.getAttribute("user");

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

		return "order/order_confirm";
	}

	@RequestMapping("/orderCo")
	public String orderCo() {
		return "/order/order_confirm";
	}

	/**
	 * 注文完了画面に遷移
	 * 
	 * @param form
	 * @return 完了画面
	 */
	@RequestMapping("/order")
	public String orderCompletion(@Validated OrderForm form, BindingResult result, Model model) {
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

		// 本日の日付を入手し配達時間をセット
		Date checkTime = new Date();
		Calendar timePlusThree = Calendar.getInstance();

		timePlusThree.setTime(checkTime);
		timePlusThree.add(Calendar.HOUR_OF_DAY, 3);
		checkTime = timePlusThree.getTime();

		SimpleDateFormat year = new SimpleDateFormat("yyyy");
		SimpleDateFormat month = new SimpleDateFormat("MM");
		SimpleDateFormat day = new SimpleDateFormat("dd");

		Integer deliveryYear = Integer.parseInt(year.format(timePlusThree.getTime()));
		Integer deliveryMonth = Integer.parseInt(month.format(form.getOrderDate().getTime()));
		Integer deliveryDay = Integer.parseInt(day.format(form.getOrderDate().getTime()));

		@SuppressWarnings("deprecation")
		Date deliveryTime = new Date(deliveryYear - 1900, deliveryMonth - 1, deliveryDay, form.getIntegerDeliveryTime(),
				00, 00);

		if (checkTime.after(deliveryTime)) {
			model.addAttribute("errorDeliveryDate", "今から3時間後の日時をご入力ください");
			return "/order/order_confirm";
		}

		User user = (User) session.getAttribute("user");
		Order order = cartService.getCartByUserId(user.getId());

		if (order == null) {
			return "redirect:/toOrder"; // 万が一カートが取れなかった場合
		}

		BeanUtils.copyProperties(form, order);

		// 郵便番号のハイフンを消してドメインにセット
		order.setDestinationZipcode(form.getDestinationZipcode().replace("-", ""));

		order.setDeliveryTime(form.getTimestamp());
		service.order(order);
		// 完了メールを送信

		service.sendMail(order, user.getEmail());

		session.removeAttribute("cartItemList");

		session.removeAttribute("totalPrice");

		return "redirect:/orderCompletion";
	}

	@RequestMapping("orderCompletion")
	public String orderCompletion() {
		return "/order/order_finished";
	}

	/**
	 * 注文履歴のページを表示
	 * 
	 * @param model リクエストスコープ
	 * @return 注文履歴
	 */
	@RequestMapping("/orderHistory")
	public String orderHistory(Model model) {

		// ユーザーの情報を拾ってくる
		User user = (User) session.getAttribute("user");
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
		System.out.println(orderList);

		return "order/order_history";
	}

	@RequestMapping("orderdetail")
	public String orderDetail(Integer id, Model model) {
		System.out.println(id);
		List<Order> orderList = service.orderLoad(id);
		model.addAttribute("orderList", orderList);
		System.out.println(orderList);
		return "/order/order_detail";
	}
}
