package com.example.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.Item;
import com.example.domain.Topping;

public class ItemDetailResponse {
/**id */
private Integer id;
/**商品名 */
private String name;
/**商品説明 */
private String description;
/**Mサイズの値段 */
private Integer priceM;
/**Lサイズの値段 */
private Integer priceL;
/**商品画像 */
private String imagePath;
/**トッピングリスト */
private List<ToppingResponse> toppings;
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
public String getDescription() {
    return description;
}
public void setDescription(String description) {
    this.description = description;
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
public List<ToppingResponse> getToppings() {
    return toppings;
}
public void setToppings(List<ToppingResponse> toppings) {
    this.toppings = toppings;
}
@Override
public String toString() {
    return "ItemDetailResponse [id=" + id + ", name=" + name + ", description=" + description + ", priceM=" + priceM
            + ", priceL=" + priceL + ", imagePath=" + imagePath + ", toppings=" + toppings + "]";
}
public ItemDetailResponse(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription(); 
        this.priceM = item.getPriceM();
        this.priceL = item.getPriceL();
        this.imagePath = item.getImagePath();

        if(item.getToppingList() !=null){
    List<ToppingResponse> dtoList=new ArrayList<>();
    for (Topping  t:item.getToppingList()){
        dtoList.add(new ToppingResponse(t));
    }
    this.toppings=dtoList;
  }
}

  
}
