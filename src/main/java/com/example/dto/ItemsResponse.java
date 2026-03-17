package com.example.dto;

import com.example.domain.Item;

public class ItemsResponse {
    /**id */
    private Integer id;
    /**商品名 */
    private String name;
    /**Mサイズの値段 */
    private Integer priceM;
    /**Lサイズの値段 */
    private Integer priceL;
    /**商品画像 */
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

    public ItemsResponse(Item item){
        this.id=item.getId();
        this.name=item.getName();
        this.priceM=item.getPriceM();
        this.priceL=item.getPriceL();
        this.imagePath=item.getImagePath();
    }

}
