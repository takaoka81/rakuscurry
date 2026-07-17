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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + priceM;
		result = prime * result + priceL;
		result = prime * result + ((imagePath == null) ? 0 : imagePath.hashCode());
		result = prime * result + ((deleted == null) ? 0 : deleted.hashCode());
		result = prime * result + ((toppingList == null) ? 0 : toppingList.hashCode());
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
		Item other = (Item) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (priceM != other.priceM)
			return false;
		if (priceL != other.priceL)
			return false;
		if (imagePath == null) {
			if (other.imagePath != null)
				return false;
		} else if (!imagePath.equals(other.imagePath))
			return false;
		if (deleted == null) {
			if (other.deleted != null)
				return false;
		} else if (!deleted.equals(other.deleted))
			return false;
		if (toppingList == null) {
			if (other.toppingList != null)
				return false;
		} else if (!toppingList.equals(other.toppingList))
			return false;
		return true;
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

	public Object getSize() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getSize'");
	}
	
	
}
