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

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + userId;
		result = prime * result + status;
		result = prime * result + totalPrice;
		result = prime * result + ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result + ((destinationName == null) ? 0 : destinationName.hashCode());
		result = prime * result + ((destinationEmail == null) ? 0 : destinationEmail.hashCode());
		result = prime * result + ((destinationZipcode == null) ? 0 : destinationZipcode.hashCode());
		result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
		result = prime * result + ((destinationTel == null) ? 0 : destinationTel.hashCode());
		result = prime * result + ((deliveryTime == null) ? 0 : deliveryTime.hashCode());
		result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((orderItemList == null) ? 0 : orderItemList.hashCode());
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
		Order other = (Order) obj;
		if (id != other.id)
			return false;
		if (userId != other.userId)
			return false;
		if (status != other.status)
			return false;
		if (totalPrice != other.totalPrice)
			return false;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (destinationName == null) {
			if (other.destinationName != null)
				return false;
		} else if (!destinationName.equals(other.destinationName))
			return false;
		if (destinationEmail == null) {
			if (other.destinationEmail != null)
				return false;
		} else if (!destinationEmail.equals(other.destinationEmail))
			return false;
		if (destinationZipcode == null) {
			if (other.destinationZipcode != null)
				return false;
		} else if (!destinationZipcode.equals(other.destinationZipcode))
			return false;
		if (destinationAddress == null) {
			if (other.destinationAddress != null)
				return false;
		} else if (!destinationAddress.equals(other.destinationAddress))
			return false;
		if (destinationTel == null) {
			if (other.destinationTel != null)
				return false;
		} else if (!destinationTel.equals(other.destinationTel))
			return false;
		if (deliveryTime == null) {
			if (other.deliveryTime != null)
				return false;
		} else if (!deliveryTime.equals(other.deliveryTime))
			return false;
		if (paymentMethod == null) {
			if (other.paymentMethod != null)
				return false;
		} else if (!paymentMethod.equals(other.paymentMethod))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (orderItemList == null) {
			if (other.orderItemList != null)
				return false;
		} else if (!orderItemList.equals(other.orderItemList))
			return false;
		return true;
	}
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
