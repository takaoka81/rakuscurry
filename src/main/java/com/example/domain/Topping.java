package com.example.domain;

/**
 * トッピングのドメイン
 * 
 * @author naramasato
 *
 */
public class Topping {

	//id
	private Integer id;
	//トッピング名
	private String name;
	//Mサイズ時の価格
	private int priceM;
	//Lサイズ時の価格
	private int priceL;
	
	//コンストラクター
	public Topping() {}
	
	public Topping(Integer id, String name, Integer priceM, Integer priceL) {
		super();
		this.id = id;
		this.name = name;
		this.priceM = priceM;
		this.priceL = priceL;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + priceM;
		result = prime * result + priceL;
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
		Topping other = (Topping) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (priceM != other.priceM)
			return false;
		if (priceL != other.priceL)
			return false;
		return true;
	}

	//ゲッターとセッター
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

	//toString
	@Override
	public String toString() {
		return "Topping [id=" + id + ", name=" + name + ", priceM=" + priceM + ", priceL=" + priceL + "]";
	}
	
	
}
