package com.example.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.CartItem;
import com.example.domain.Topping;
import com.example.form.ItemCartInForm;
import com.example.service.CartService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * @author satakemisako
 * カートに商品を追加する
 *
 */
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
	
	//Cartに商品を追加
	@RequestMapping("/inCart")
	public String inCart(ItemCartInForm form) {
		
		CartItem cartItem = new CartItem();
		BeanUtils.copyProperties(form,cartItem);
		cartItem.setItemId(form.getId());
			
		//cartItemにitemの金額を設置
		cartItem.setItemPrice(service.getPriceSize(form));
		
		//トッピングをcartItemに代入
		@SuppressWarnings("unchecked")
		List<Topping> toppingList = (List<Topping>) application.getAttribute("toppingList");
		List<Topping> toppings = service.getToppingIndex(toppingList, form.getToppingIndex());
		cartItem.setToppingList(toppings);
		
		//小計を代入
//		Integer subPrices = service.calcSubTotal(cartItem);
//		cartItem.setSubTotal(subPrices);
			
		//カート内の商品をリストに格納
		//初めてセッションスコープに格納する際はLinkedListを入れる
		if(session.getAttribute("cartItemList")==null) {
			List<CartItem> cartItemList = new LinkedList<>();
			session.setAttribute("cartItemList", cartItemList);
		}
			
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");
		cartItemList.add(cartItem);
		session.setAttribute("cartItemList", cartItemList);
			
		return "redirect:/showCart";
	}
	
	//Cartの中身を表示するメソッド
	@RequestMapping("/showCart")
	public String showCart(Model model) {
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");
		
		int totalPrice = 0;
		if(cartItemList == null) {
			session.setAttribute("cartItemList", new LinkedList<>());
			model.addAttribute("cartNothing", "カートの中身はございません");
		} else if(cartItemList.size() == 0) {
			model.addAttribute("cartNothing", "カートの中身はございません");
		}
		else {
			totalPrice = service.calcTotal(cartItemList);
		}
		session.setAttribute("totalPrice", totalPrice);
		return "cart/cart_list";
	}
	
	@RequestMapping("/delete")
	public String delete(String index, Model model) {
		System.out.println(index);
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");
		cartItemList.remove(Integer.parseInt(index));
		return showCart(model);
	}
	
}
