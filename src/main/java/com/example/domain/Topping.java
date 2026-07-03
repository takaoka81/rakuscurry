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
