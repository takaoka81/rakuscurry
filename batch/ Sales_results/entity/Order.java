package com.example.batch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 1. 最上位：注文データ（親）
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("user_id")
    private Integer userId;
    private Integer status;
    @JsonProperty("total_price")
    private Integer totalPrice;
    @JsonProperty("order_date")
    private LocalDate orderDate;
    @JsonProperty("destination_name")
    private String destinationName;
    @JsonProperty("destination_email")
    private String destinationEmail;
    @JsonProperty("destination_zipcode")
    private String destinationZipcode;
    @JsonProperty("destination_address")
    private String destinationAddress;
    @JsonProperty("destination_tel")
    private String destinationTel;
    @JsonProperty("delivery_time")
    private LocalDateTime deliveryTime;
    @JsonProperty("payment_method")
    private Integer paymentMethod;
    
    // 中間層のリストを持つ
    @JsonProperty("items")
    public List<OrderItem> items;

public Order(){}

public Order(Integer id, Integer userId, Integer status, Integer totalPrice, LocalDate orderDate,
        String destinationName, String destinationEmail, String destinationZipcode, String destinationAddress,
        String destinationTel, LocalDateTime deliveryTime, Integer paymentMethod, List<OrderItem> items) {
    this.id = id;
    this.userId = userId;
    this.status = status;
    this.totalPrice = totalPrice;
    this.orderDate = orderDate;
    this.destinationName = destinationName;
    this.destinationEmail = destinationEmail;
    this.destinationZipcode = destinationZipcode;
    this.destinationAddress = destinationAddress;
    this.destinationTel = destinationTel;
    this.deliveryTime = deliveryTime;
    this.paymentMethod = paymentMethod;
    this.items = items;
}

public Integer getId() {
    return id;
}

public void setId(Integer id) {
    this.id = id;
}

public Integer getUserId() {
    return userId;
}

public void setUserId(Integer userId) {
    this.userId = userId;
}

public Integer getStatus() {
    return status;
}

public void setStatus(Integer status) {
    this.status = status;
}

public Integer getTotalPrice() {
    return totalPrice;
}

public void setTotalPrice(Integer totalPrice) {
    this.totalPrice = totalPrice;
}

public LocalDate getOrderDate() {
    return orderDate;
}

public void setOrderDate(LocalDate orderDate) {
    this.orderDate = orderDate;
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

public LocalDateTime getDeliveryTime() {
    return deliveryTime;
}

public void setDeliveryTime(LocalDateTime deliveryTime) {
    this.deliveryTime = deliveryTime;
}

public Integer getPaymentMethod() {
    return paymentMethod;
}

public void setPaymentMethod(Integer paymentMethod) {
    this.paymentMethod = paymentMethod;
}

public List<OrderItem> getItems() {
    return items;
}

public void setItems(List<OrderItem> items) {
    this.items = items;
}

@Override
public String toString() {
    return "Order [id=" + id + ", userId=" + userId + ", status=" + status + ", totalPrice=" + totalPrice
            + ", orderDate=" + orderDate + ", destinationName=" + destinationName + ", destinationEmail="
            + destinationEmail + ", destinationZipcode=" + destinationZipcode + ", destinationAddress="
            + destinationAddress + ", destinationTel=" + destinationTel + ", deliveryTime=" + deliveryTime
            + ", paymentMethod=" + paymentMethod + ", items=" + items + "]";
}




    


    
}
