package com.example.domain;

import java.util.List;

public class OrderItem {

	// id
	private int id;
	// 商品id
	private int itemId;
	// orderのid
	private int orderId;
	// 数量
	private int quantity;
	// サイズ
	private String size;
	// 注文時金額
	private Integer orderPrice = 0;
	// 小計
	// private Integer subTotal;
	// item
	private Item item;
	// 注文したトッピングのList
	private List<OrderTopping> orderTopping;

	// 割引額
	private Integer discount;

	private Integer freeCount;

	private boolean isFree;

	// ゲッターとセッター
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
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

		// 1. 商品自体の単価を決める（nullなら0円）
		int itemPrice = (this.orderPrice != null) ? this.orderPrice : 0;

		// 2. トッピングの合計金額を計算する
		int toppingTotalPrice = 0;
		// ★ここを orderToppingList に修正
		if (this.orderTopping != null) {
			for (OrderTopping topping : this.orderTopping) {
				if (topping.getOrderPrice() != null) {
					toppingTotalPrice += topping.getOrderPrice();
				}
			}
		}

		// 3. 数量を確認する（nullなら0個）
		int count = this.quantity;

		if (freeCount != null) {
			return (itemPrice + toppingTotalPrice) * count - (itemPrice * freeCount);
		}

		if (discount > 0 && discount != null) {
			return (itemPrice + toppingTotalPrice) * count - discount;
		}
		// 4. 計算結果を返す
		return (itemPrice + toppingTotalPrice) * count;
	}

	public Integer getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(Integer freeCount) {
		this.freeCount = freeCount;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

}
