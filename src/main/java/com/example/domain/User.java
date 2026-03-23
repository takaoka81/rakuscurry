package com.example.domain;

/**
 * 
 * @author satakemisako
 *         ユーザー情報のドメインクラス
 *
 */
public class User {

	// ID
	private Integer id;
	// 状態
	private Integer status;
	// ユーザー氏名
	private String name;
	// パスワード
	private String password;
	// メールアドレス
	private String email;
	// 郵便番号
	private String zipcode;
	// 住所
	private String address;
	// 電話番号
	private String telephone;

	/**
	 * 現在のスタンプ数
	 */
	private Integer stampNowCount;

	/**
	 * 今まで貯めたスタンプの総数
	 */
	private Integer stampAllCount;

	// 引数ありコンストラクタ
	public User(Integer id, Integer status, String name, String password, String email, String zipcode, String address,
			String telephone, Integer stampNowCount, Integer stampAllCount) {
		super();
		this.id = id;
		this.status = status;
		this.name = name;
		this.password = password;
		this.email = email;
		this.zipcode = zipcode;
		this.address = address;
		this.telephone = telephone;
		this.stampNowCount = stampNowCount;
		this.stampAllCount = stampAllCount;
	}

	// 引数なしコンストラクタ
	public User() {
	}

	// 以下getter及びsetter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getStampNowCount() {
		return stampNowCount;
	}

	public void setStampNowCount(Integer stampNowCount) {
		this.stampNowCount = stampNowCount;
	}

	public Integer getStampAllCount() {
		return stampAllCount;
	}

	public void setStampAllCount(Integer stampAllCount) {
		this.stampAllCount = stampAllCount;
	}

}
