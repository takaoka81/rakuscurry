package com.example.domain;

import java.util.List;

public class OrderItem {

	// id
	private Integer id;
	// 商品id
	private Integer itemId;
	// orderのid
	private Integer orderId;
	// 数量
	private Integer quantity;
	// サイズ
	private String size;
	// 注文時金額
	private Integer orderPrice;
	// 小計
	// private Integer subTotal;
	// item
	private Item item;
	// 注文したトッピングのList
	private List<OrderTopping> orderTopping;

	// ゲッターとセッター
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Integer getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<OrderTopping> getOrderTopping() {
		return orderTopping;
	}

	public void setOrderTopping(List<OrderTopping> orderTopping) {
		this.orderTopping = orderTopping;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", itemId=" + itemId + ", orderId=" + orderId + ", quantity=" + quantity
				+ ", size=" + size + ", orderPrice=" + orderPrice + ", item=" + item + ", orderTopping=" + orderTopping
				+ "]";
	}

	public Integer getSubTotal() {

		// 1. 商品自体の単価を決める（nullなら0円として扱う）
		int itemPrice = 0;
		if (this.orderPrice != null) {
			itemPrice = this.orderPrice;
		}

		// 2. トッピングの合計金額を計算する
		int toppingTotalPrice = 0;
		if (this.orderTopping != null) {
			// トッピングリストを1つずつチェック
			for (OrderTopping topping : this.orderTopping) {
				// トッピングの価格が設定されていれば加算
				if (topping.getOrderPrice() != null) {
					toppingTotalPrice = toppingTotalPrice + topping.getOrderPrice();
				}
			}
		}

		// 3. 数量を確認する（nullなら0個として扱う）
		int count = 0;
		if (this.quantity != null) {
			count = this.quantity;
		}

		// 4. 【 (商品単価 + トッピング合計) × 数量 】を計算して返す
		int result = (itemPrice + toppingTotalPrice) * count;
		return result;
	}
}
