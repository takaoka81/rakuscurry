package com.example.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.common.SessionCart;
import com.example.domain.CartItem;
import com.example.domain.Item;
import com.example.domain.LoginUserDetails;
import com.example.domain.Order;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.form.AddItemsForm;
import com.example.form.ItemCartInForm;
import com.example.service.CartService;
import com.example.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class CartController {
	private final CartService service;

	private final ItemService itemService;

	private final HttpSession session;

	private final SessionCart sessionCart;

	private final ObjectMapper objectMapper;

	public ItemCartInForm setupForm() {
		return new ItemCartInForm();
	}

	@RequestMapping("/inCart")
	public String inCart(@AuthenticationPrincipal LoginUserDetails loginUserDetails, ItemCartInForm form) {
		CartItem cartItem = CartItem.form(form, service);

		List<Topping> toppingList = itemService.findAllTopping();
		List<Topping> selectedToppings = service.getToppingIndex(toppingList, form.getToppingIndex());
		cartItem.setToppingList(selectedToppings);

		if (loginUserDetails != null) {
			User user = loginUserDetails.getUser();
			service.addItemToCart(cartItem, user.getId());
		} else {
			sessionCart.getItems().add(cartItem);
		}

		return "redirect:/showCart";
	}

	@RequestMapping("/showCart")
	public String showCart(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Model model) {

		model.addAttribute("itemListJson", buildItemListJson());
		model.addAttribute("toppingListJson", buildToppingListJson());

		if (loginUserDetails != null) {
			// --- ログイン時の処理 ---
			User user = loginUserDetails.getUser();
			Order order = service.getCartByUserId(user.getId());
			if (order == null || order.getOrderItemList().isEmpty()) {
				model.addAttribute("cartNothing", "カートに商品がありません");
			} else {
				model.addAttribute("order", order);
			}
		} else {
			// --- 未ログイン時の処理（カッコの構造を修正） ---
			List<CartItem> cartItemList = sessionCart.getItems();
			model.addAttribute("cartItemList", cartItemList);

			if (cartItemList.isEmpty()) {
				model.addAttribute("cartNothing", "カートに商品がありません");
				session.setAttribute("totalPrice", 0);
			} else {
				int total = cartItemList.stream()
						.mapToInt(item -> item.getSubTotal())
						.sum();

				// /* 修正点：ここでの保存がHTMLの表示に直結します */
				session.setAttribute("totalPrice", total);
			}
		}
		return "cart/cart_list";
	}

	@RequestMapping("/delete")
	public String delete(@AuthenticationPrincipal LoginUserDetails loginUserDetails, Integer index,
			Integer orderItemId) {

		if (loginUserDetails != null) {
			User user = loginUserDetails.getUser();
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
			List<CartItem> cartItemList = sessionCart.getItems();

			if (index != null && index < cartItemList.size()) {
				cartItemList.remove(index.intValue());

				int total = 0;
				for (CartItem item : cartItemList) {
					total += item.getSubTotal();
				}
				// 未ログイン時はここでセッションを更新しているため、反映されます
				session.setAttribute("totalPrice", total);
			}
		}

		return "redirect:/showCart";
	}

	@RequestMapping("/addItems")
	public String addItems(@AuthenticationPrincipal LoginUserDetails loginUserDetails, AddItemsForm form) {

		if (form.getItems() != null) {
			for (AddItemsForm.Entry entry : form.getItems()) {
				if (entry == null || entry.getProductId() == null || entry.getQuantity() == null) {
					continue;
				}

				Item item = itemService.showItemDetail(entry.getProductId());
				if (item == null) {
					continue;
				}
				String size = entry.getSize() != null ? entry.getSize() : "M";

				CartItem cartItem = new CartItem();
				cartItem.setItemId(item.getId());
				cartItem.setName(item.getName());
				cartItem.setImagePath(item.getImagePath());
				cartItem.setSize(size);
				cartItem.setItemPrice("L".equals(size) ? item.getPriceL() : item.getPriceM());
				cartItem.setQuantity(entry.getQuantity());
				cartItem.setToppingList(resolveToppings(entry.getToppingIds()));

				if (loginUserDetails != null) {
					User user = loginUserDetails.getUser();
					service.addItemToCart(cartItem, user.getId());
				} else {
					sessionCart.getItems().add(cartItem);
				}
			}
		}

		return "redirect:/showCart";
	}

	/**
	 * 商品一覧をJS側でセレクトボックスを組み立てるためのJSON文字列に変換する
	 */
	private String buildItemListJson() {
		List<Map<String, Object>> options = itemService.findAll().stream()
				.filter(item -> !Boolean.TRUE.equals(item.getDeleted()))
				.map(item -> Map.<String, Object>of("id", item.getId(), "name", item.getName()))
				.collect(Collectors.toList());

		try {
			return objectMapper.writeValueAsString(options);
		} catch (JsonProcessingException e) {
			return "[]";
		}
	}

	/**
	 * トッピング一覧をJS側でチェックボックスを組み立てるためのJSON文字列に変換する
	 */
	private String buildToppingListJson() {
		List<Map<String, Object>> options = itemService.findAllTopping().stream()
				.map(topping -> Map.<String, Object>of("id", topping.getId(), "name", topping.getName()))
				.collect(Collectors.toList());

		try {
			return objectMapper.writeValueAsString(options);
		} catch (JsonProcessingException e) {
			return "[]";
		}
	}

	/**
	 * 選択されたトッピングidから、実際のTopping一覧を絞り込む
	 */
	private List<Topping> resolveToppings(List<Integer> toppingIds) {
		if (toppingIds == null || toppingIds.isEmpty()) {
			return Collections.emptyList();
		}

		return itemService.findAllTopping().stream()
				.filter(topping -> toppingIds.contains(topping.getId()))
				.collect(Collectors.toList());
	}

}
