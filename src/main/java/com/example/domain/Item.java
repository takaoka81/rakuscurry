package com.example.domain;

import java.util.Collections;
import java.util.List;

public class Item {
	/**
	 * 商品を表すドメイン
	 */

	/** id */
	private int id;
	/** 商品名 */
	private String name;
	/** 商品説明 */
	private String description;
	/** Mサイズの値段 */
	private int priceM;
	/** Lサイズの値段 */
	private int priceL;
	/** 商品画像 */
	private String imagePath;
	/** 削除フラグ */
	private Boolean deleted;
	/** トッピングリスト */
	private List<Topping> toppingList;
	
	
	/** 引数なし */
	public Item() {
	}
	
	/** 引数あり */
	public Item(Integer id, String name, String description, Integer priceM, Integer priceL, String imagePath,
			Boolean deleted, List<Topping> toppingList) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.priceM = priceM;
		this.priceL = priceL;
		this.imagePath = imagePath;
		this.deleted = deleted;
		this.toppingList = List.copyOf(toppingList);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getPriceM() {
		return priceM;
	}

	public void setPriceM(int priceM) {
		this.priceM = priceM;
	}

	public int getPriceL() {
		return priceL;
	}

	public void setPriceL(int priceL) {
		this.priceL = priceL;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<Topping> getToppingList() {
		return Collections.unmodifiableList(toppingList); 
	}

	public void setToppingList(List<Topping> toppingList) {
		this.toppingList = List.copyOf(toppingList);
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", description=" + description + ", priceM=" + priceM + ", priceL="
				+ priceL + ", imagePath=" + imagePath + ", deleted=" + deleted + ", toppingList=" + toppingList + "]";
	}
	
	
}
