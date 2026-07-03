package com.example.domain;

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
