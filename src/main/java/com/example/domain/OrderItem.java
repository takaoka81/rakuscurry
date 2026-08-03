package com.example.domain;

import java.util.Collections;
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

	// 静的ファクトリーメソッド
	public static OrderItem form(CartItem cartItem, Integer orderId){
		OrderItem orderItem = new OrderItem();
		orderItem.itemId = cartItem.getItemId();
		orderItem.orderId = orderId;
		orderItem.quantity = cartItem.getQuantity();
		orderItem.size = cartItem.getSize();
		orderItem.orderPrice = cartItem.getItemPrice();
		return orderItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + itemId;
		result = prime * result + orderId;
		result = prime * result + quantity;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((orderPrice == null) ? 0 : orderPrice.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((orderTopping == null) ? 0 : orderTopping.hashCode());
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((freeCount == null) ? 0 : freeCount.hashCode());
		result = prime * result + (isFree ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderItem other = (OrderItem) obj;
		if (id != other.id)
			return false;
		if (itemId != other.itemId)
			return false;
		if (orderId != other.orderId)
			return false;
		if (quantity != other.quantity)
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (orderPrice == null) {
			if (other.orderPrice != null)
				return false;
		} else if (!orderPrice.equals(other.orderPrice))
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (orderTopping == null) {
			if (other.orderTopping != null)
				return false;
		} else if (!orderTopping.equals(other.orderTopping))
			return false;
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (freeCount == null) {
			if (other.freeCount != null)
				return false;
		} else if (!freeCount.equals(other.freeCount))
			return false;
		if (isFree != other.isFree)
			return false;
		return true;
	}

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
		return Collections.unmodifiableList(orderTopping);
	}

	public void setOrderTopping(List<OrderTopping> orderTopping) {
		this.orderTopping = List.copyOf(orderTopping);
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

		if (discount != null && discount > 0) {
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
