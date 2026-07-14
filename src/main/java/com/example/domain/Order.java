package com.example.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * Orderのドメイン
 * @author naramasato
 *
 */
public class Order {

	//id
	private int id;
	//ユーザーid
	private int userId;
	//状態
	private int status;
	//合計金額
	private int totalPrice;
	//注文日
	private Date orderDate;
	//宛先氏名
	private String destinationName;
	//宛先Eメール
	private String destinationEmail;
	//宛先郵便番号
	private String destinationZipcode;
	//宛先住所
	private String destinationAddress;
	//宛先TEL
	private String destinationTel;
	//配達時間
	private Timestamp deliveryTime;
	//支払方法
	private Integer paymentMethod;
	//ユーザー
	private User user;
	//OrderItemのリスト
	private List<OrderItem> orderItemList;
	
	//ゲッターとセッター
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Date getOrderDate() {
		return orderDate == null ? null : new Date(orderDate.getTime());
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate == null ? null : new Date(orderDate.getTime());
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationEmail() {
		return destinationEmail;
	}
	public void setDestinationEmail(String destinationEmail) {
		this.destinationEmail = destinationEmail;
	}
	public String getDestinationZipcode() {
		return destinationZipcode;
	}
	public void setDestinationZipcode(String destinationZipcode) {
		this.destinationZipcode = destinationZipcode;
	}
	public String getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	public String getDestinationTel() {
		return destinationTel;
	}
	public void setDestinationTel(String destinationTel) {
		this.destinationTel = destinationTel;
	}
	public Timestamp getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Timestamp deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<OrderItem> getOrderItemList() {
		return List.copyOf(orderItemList);
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = List.copyOf(orderItemList);
	}
	
	@Override
	public String toString() {
		return "Order [id=" + id + ", userId=" + userId + ", status=" + status + ", totalPrice=" + totalPrice
				+ ", orderDate=" + orderDate + ", destinationName=" + destinationName + ", destinationEmail="
				+ destinationEmail + ", destinationZipcode=" + destinationZipcode + ", destinationAddress="
				+ destinationAddress + ", destinationTel=" + destinationTel + ", deliveryTime=" + deliveryTime
				+ ", paymentMethod=" + paymentMethod + ", user=" + user + ", orderItemList=" + orderItemList + "]";
	}
	
}
