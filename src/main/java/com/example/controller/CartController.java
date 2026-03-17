package com.example.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.form.ItemCartInForm;
import com.example.service.CartService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class CartController {

	@Autowired
	private CartService service;

	@Autowired
	private HttpSession session;
	@Autowired
	private ServletContext application;

	public ItemCartInForm setupForm() {
		return new ItemCartInForm();
	}

	@RequestMapping("/inCart")
	public String inCart(ItemCartInForm form) {
		CartItem cartItem = new CartItem();
		BeanUtils.copyProperties(form, cartItem);
		cartItem.setItemId(form.getId());
		cartItem.setItemPrice(service.getPriceSize(form));

		@SuppressWarnings("unchecked")
		List<Topping> toppingList = (List<Topping>) application.getAttribute("toppingList");
		List<Topping> selectedToppings = service.getToppingIndex(toppingList, form.getToppingIndex());
		cartItem.setToppingList(selectedToppings);

		User user = (User) session.getAttribute("user");
		if (user != null) {
			service.addItemToCart(cartItem, user.getId());
		} else {
			saveToSessionCart(cartItem);
		}

		return "redirect:/showCart";
	}

	private void saveToSessionCart(CartItem cartItem) {
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");
		if (cartItemList == null) {
			cartItemList = new LinkedList<>();
		}
		cartItemList.add(cartItem);
		session.setAttribute("cartItemList", cartItemList);
	}

	@RequestMapping("/showCart")
	public String showCart(Model model) {
		User user = (User) session.getAttribute("user");

		if (user != null) {
			// --- ログイン時の処理 ---
			Order order = service.getCartByUserId(user.getId());
			if (order == null || order.getOrderItemList().isEmpty()) {
				model.addAttribute("cartNothing", "カートに商品がありません");
			} else {
				model.addAttribute("order", order);
			}
		} else {
			// --- 未ログイン時の処理（カッコの構造を修正） ---
			@SuppressWarnings("unchecked")
			List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");

			if (cartItemList == null || cartItemList.isEmpty()) {
				model.addAttribute("cartNothing", "カートに商品がありません");
				session.setAttribute("totalPrice", 0);
			} else {
				int total = 0;
				for (CartItem item : cartItemList) {
					total += item.getSubTotal();
				}
				// /* 修正点：ここでの保存がHTMLの表示に直結します */
				session.setAttribute("totalPrice", total);
			}
		}
		return "cart/cart_list";
	}

	@RequestMapping("/delete")
	public String delete(Integer index, Integer orderItemId) {
		User user = (User) session.getAttribute("user");

		if (user != null) {
			if (orderItemId != null) {
				// 1. DBから削除 & DB上の合計金額を更新
				service.deleteOrderItem(orderItemId, user.getId());

				// 2. ★追加：最新の注文情報をDBから取得し直す
				Order order = service.getCartByUserId(user.getId());

				// 3. ★重要：セッションの totalPrice を最新の注文合計で上書きする
				if (order != null) {
					session.setAttribute("totalPrice", order.getTotalPrice());
				} else {
					session.setAttribute("totalPrice", 0);
				}
			}
		} else {
			// --- 未ログインの場合（Sessionから削除） ---
			@SuppressWarnings("unchecked")
			List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");

			if (cartItemList != null && index != null && index < cartItemList.size()) {
				cartItemList.remove(index.intValue());

				int total = 0;
				for (CartItem item : cartItemList) {
					total += item.getSubTotal();
				}
				// 未ログイン時はここでセッションを更新しているため、反映されます
				session.setAttribute("totalPrice", total);
				session.setAttribute("cartItemList", cartItemList);
			}
		}

		return "redirect:/showCart";
	}
}
