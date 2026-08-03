package com.example.domain;

import java.util.List;

/**
 * OrderToppingのドメイン
 * 
 * @author naramasato
 *
 */
public class OrderTopping {

	// id
	private int id;
	// トッピングid
	private int toppingId;
	// 注文商品id
	private Integer orderItemId;
	// topping情報
	private Topping topping;
	// 注文時金額
	private Integer orderPrice;

	// 静的ファクトリーメソッド
	public static List<OrderTopping> form(List<Topping> toppingList, Integer orderItemId, String size) {
		List<OrderTopping> orderToppings = toppingList.stream()
				.map(topping -> {
					OrderTopping ot = new OrderTopping();
					ot.setOrderItemId(orderItemId);
					ot.setToppingId(topping.getId());
					ot.setOrderPrice("M".equals(size)
							? topping.getPriceM()
							: topping.getPriceL());
					return ot;
				})
				.toList();
		return orderToppings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + toppingId;
		result = prime * result + ((orderItemId == null) ? 0 : orderItemId.hashCode());
		result = prime * result + ((topping == null) ? 0 : topping.hashCode());
		result = prime * result + ((orderPrice == null) ? 0 : orderPrice.hashCode());
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
		OrderTopping other = (OrderTopping) obj;
		if (id != other.id)
			return false;
		if (toppingId != other.toppingId)
			return false;
		if (orderItemId == null) {
			if (other.orderItemId != null)
				return false;
		} else if (!orderItemId.equals(other.orderItemId))
			return false;
		if (topping == null) {
			if (other.topping != null)
				return false;
		} else if (!topping.equals(other.topping))
			return false;
		if (orderPrice == null) {
			if (other.orderPrice != null)
				return false;
		} else if (!orderPrice.equals(other.orderPrice))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getToppingId() {
		return toppingId;
	}

	public void setToppingId(int toppingId) {
		this.toppingId = toppingId;
	}

	public Integer getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Topping getTopping() {
		return topping;
	}

	public void setTopping(Topping topping) {
		this.topping = topping;
	}

	public Integer getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Integer orderPrice) {
		this.orderPrice = orderPrice;
	}

	@Override
	public String toString() {
		return "OrderTopping [id=" + id + ", toppingId=" + toppingId + ", orderItemId=" + orderItemId + ", topping="
				+ topping + ", orderPrice=" + orderPrice + "]";
	}

}
