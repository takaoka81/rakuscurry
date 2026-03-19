package com.example.batch.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

    // 2. 中間層：商品データ（子）
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {
    @JsonProperty("id")
    public int id;
    
    @JsonProperty("item_id")
    public int itemId;
    
    @JsonProperty("quantity")
    public int quantity;
    
    @JsonProperty("size")
    public String size;
    
    // 一番下の層のリストを持つ
    @JsonProperty("toppings")
    public List<OrderTopping> toppings;

    public OrderItem(){}

    public OrderItem(int id, int itemId, int quantity, String size, List<OrderTopping> toppings) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.size = size;
        this.toppings = toppings;
    }

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

    public List<OrderTopping> getToppings() {
        return toppings;
    }

    public void setToppings(List<OrderTopping> toppings) {
        this.toppings = toppings;
    }

    @Override
    public String toString() {
        return "OrderItemDTO [id=" + id + ", itemId=" + itemId + ", quantity=" + quantity + ", size=" + size
                + ", toppings=" + toppings + "]";
    }

    
    
}
