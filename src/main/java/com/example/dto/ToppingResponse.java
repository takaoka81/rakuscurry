package com.example.dto;

import com.example.domain.Topping;

public class ToppingResponse {
    /**id */
    private Integer id;
    /**商品名 */
    private String name;
    /**Mサイズの値段 */
    private Integer priceM;
    /**Lサイズの値段 */
    private Integer priceL;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getPriceM() {
        return priceM;
    }
    public void setPriceM(Integer priceM) {
        this.priceM = priceM;
    }
    public Integer getPriceL() {
        return priceL;
    }
    public void setPriceL(Integer priceL) {
        this.priceL = priceL;
    }
    public ToppingResponse() {
    }
    public ToppingResponse(Integer id, String name, Integer priceM, Integer priceL) {
        this.id = id;
        this.name = name;
        this.priceM = priceM;
        this.priceL = priceL;
    }
    @Override
    public String toString() {
        return "ToppingResponse [id=" + id + ", name=" + name + ", priceM=" + priceM + ", priceL=" + priceL + "]";
    }
    public ToppingResponse(Topping topping) {
        this.id = topping.getId();
        this.name = topping.getName();
        this.priceM = topping.getPriceM();
        this.priceL=topping.getPriceL();
    }

}
