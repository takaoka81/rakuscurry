package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.example.service.OrderService;

import jakarta.servlet.http.HttpSession;
/**
 * 注文確認画面に遷移するためのコントローラー
 * @author MatsunagaDai,MiyazawaNami
 *
 */
@Controller
@RequestMapping("")
public class OrderControler {

	private static final Logger logger = LoggerFactory.getLogger(OrderControler.class);
	
	@ModelAttribute
	public OrderForm setOrderForm() {
		return new OrderForm();
	}
	
	@Autowired
	private OrderService service;

	@Autowired
	private HttpSession session;
	
	public OrderForm setUpOrderForm() {
		return new OrderForm();
	}
	
	@RequestMapping("/toOrder")
	public String toOrder() {
		
		//セッションにログイン情報があれば確認画面に遷移
		//ログイン情報がなければログイン画面に遷移
		if(session.getAttribute("user") == null) {
			return "redirect:/toLogin";
		} else {
			return "order/order_confirm";
		}
		
	}
	
	@RequestMapping("/orderCo")
	public String orderCo() {
		return "/order/order_confirm";
	}
	/**
	 * 注文完了画面に遷移
	 * @param form
	 * @return　完了画面
	 */
	@RequestMapping("/order")
	public String orderCompletion(@Validated OrderForm form, BindingResult result, Model model) {
		//昨日の日付を取得し配達日と比較
		Date date = new Date();
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(date);
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		date = yesterday.getTime();
		
		if(result.hasErrors()) {
			//illigalArgumentExsepsionで捕まるのでif文の中に記載
			if(form.getOrderDate() == null) {
				model.addAttribute("errorDeliveryDate", "配達日を入力してください");
				return "/order/order_confirm";
			} 
			if(date.after(form.getOrderDate())) {
				model.addAttribute("errorDeliveryDate", "配達日が過去の日付になっています");
				return "/order/order_confirm";
			} 
			
			return "/order/order_confirm";
		}
		
		if(date.after(form.getOrderDate())) {
			model.addAttribute("errorDeliveryDate", "配達日が過去の日付になっています");
			return "/order/order_confirm";
		} 
		
		
		//本日の日付を入手し配達時間をセット
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
		Date deliveryTime = new Date(deliveryYear-1900, deliveryMonth-1, deliveryDay, form.getIntegerDeliveryTime(), 00, 00);
		
		if(checkTime.after(deliveryTime)) {
			model.addAttribute("errorDeliveryDate", "今から3時間後の日時をご入力ください");
			return "/order/order_confirm";
		}
		
		Order order = new Order();
		BeanUtils.copyProperties(form, order);
		
		//郵便番号のハイフンを消してドメインにセット
		order.setDestinationZipcode(form.getDestinationZipcode().replace("-", ""));
		
		order.setDeliveryTime(form.getTimestamp());
		service.order(order);
		//セッションからuser情報を取得し完了メールを送信
		User user = (User)session.getAttribute("user");
		service.sendMail(user.getEmail());
		
		session.removeAttribute("cartItemList");
		
		return "redirect:/orderCompletion";
	}
	
	@RequestMapping("orderCompletion")
	public String orderCompletion() {
		return "/order/order_finished";
	}
	
	/**
	 * 注文履歴のページを表示
	 * @param model リクエストスコープ
	 * @return　注文履歴
	 */
	@RequestMapping("/orderHistory")
	public String orderHistory(Model model) {

		//ユーザーの情報を拾ってくる　
		User user = (User)session.getAttribute("user");
		//もしログインしていなければログインに戻す
		if(user == null) {
			session.setAttribute("returnUrl", "/orderHistory");
			return "forward:/toLogin";
		}
		
		
		List<Order> orderList = service.findByOrder(user.getId());
		if(orderList == null) {
			model.addAttribute("orderNothing","注文履歴がありません");
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
		model.addAttribute("orderList",orderList);
		logger.info("orderList={}", orderList);
		return "/order/order_detail";
	}
}
