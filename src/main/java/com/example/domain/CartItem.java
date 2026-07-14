package com.example.domain;

import java.util.Collections;
import java.util.List;

public class CartItem {

	// 商品Id
	private Integer itemId;
	// 商品名
	private String name;
	// 商品サイズ
	private String size;
	// 商品画像
	private String imagePath;
	// トッピングのList
	private List<Topping> toppingList;
	// 小計金額
	// private Integer subTotal;
	// 数量
	private Integer quantity;
	// 商品の元々の金額
	private Integer itemPrice;

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Integer itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<Topping> getToppingList() {
		return Collections.unmodifiableList(toppingList);
	}

	public void setToppingList(List<Topping> topping) {
		this.toppingList = List.copyOf(topping);
	}

	public Integer getSubTotal() {
		// 1. 商品自体の単価を決める（nullなら0円として扱う）
		int baseItemPrice = 0;
		if (this.itemPrice != null) {
			baseItemPrice = this.itemPrice;
		}

		// 2. トッピングの合計金額を計算する
		int toppingTotalPrice = 0;
		if (this.toppingList != null) {
			for (Topping topping : this.toppingList) {
				if ("M".equals(this.size)) {
					toppingTotalPrice += topping.getPriceM();
				} else { // "L" size
					toppingTotalPrice += topping.getPriceL();
				}
			}
		}

		// 3. 数量を確認する（nullなら0個として扱う）
		int count = (this.quantity == null) ? 0 : this.quantity;

		// 4. 【 (商品単価 + トッピング合計) × 数量 】を計算して返す
		return (baseItemPrice + toppingTotalPrice) * count;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "CartItem [itemId=" + itemId + ", name=" + name + ", size=" + size + ", imagePath=" + imagePath
				+ ", toppingList=" + toppingList + ", subTotalPrice=" + this.getSubTotal() + ", area=" + quantity
				+ ", itemPrice=" + itemPrice + "]";
	}

}
