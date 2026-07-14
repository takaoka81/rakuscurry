package com.example.common;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.example.domain.CartItem;

/**
 * 未ログインユーザーのカート情報をセッションスコープで保持するBean
 * HttpSessionへ直接Objectとして出し入れするunchecked castを避けるための置き換え
 */
@Component
@SessionScope
public class SessionCart {

	private List<CartItem> items = new LinkedList<>();

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public void clear() {
		items = new LinkedList<>();
	}
}
