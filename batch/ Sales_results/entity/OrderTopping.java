package com.example.batch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 3. 一番下の層：トッピングデータ（孫）
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderTopping {
    @JsonProperty("id")
    public int id;
    
    @JsonProperty("topping_id")
    public int toppingId;

    @JsonProperty("order_item_id")
    private Integer orderItemId;

    private static final Logger logger = LoggerFactory.getLogger(OrderTopping.class);

    
    public OrderTopping(){
        logger.debug("OrderTopping initialized (empty constructor)");
    }

    public OrderTopping(int id, int toppingId, Integer orderItemId) {
        this.id = id;
        this.toppingId = toppingId;
        this.orderItemId = orderItemId;
        logger.info("OrderTopping initialized with ID: {}, ToppingID: {}", id, toppingId);
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



    @Override
    public String toString() {
        return "OrderTopping [id=" + id + ", toppingId=" + toppingId + ", orderItemId=" + orderItemId + "]";
    }

    
    

    
}   
