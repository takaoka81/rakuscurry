package com.example.dto;

public class ItemsResponse {
    private Integer id;
    private String name;
    private Integer priceM;
    private Integer priceL;
    private String imagePath;
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
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    @Override
    public String toString() {
        return "ItemResponse [id=" + id + ", name=" + name + ", priceM=" + priceM + ", priceL=" + priceL
                + ", imagePath=" + imagePath + "]";
    }
    public ItemsResponse() {
    }
    public ItemsResponse(Integer id, String name, Integer priceM, Integer priceL, String imagePath) {
        this.id = id;
        this.name = name;
        this.priceM = priceM;
        this.priceL = priceL;
        this.imagePath = imagePath;
    }

}
