package com.example.domain;

import com.example.enums.UserStatus;

/**
 *
 * @author satakemisako
 *         ユーザー情報のドメインクラス
 *
 */
public class User {

	// ID
	private int id;
	// 状態
	private UserStatus status;
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

	// 引数なしコンストラクタ
	public User() {
	}

	// 引数ありコンストラクタ
	public User(int id, UserStatus status, String name, String password, String email, String zipcode, String address,
			String telephone, Integer stampNowCount, Integer stampAllCount) {
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

	private User(Builder builder) {
		this.id = builder.id;
		this.status = builder.status;
		this.name = builder.name;
		this.password = builder.password;
		this.email = builder.email;
		this.zipcode = builder.zipcode;
		this.address = builder.address;
		this.telephone = builder.telephone;
		this.stampNowCount = builder.stampNowCount;
		this.stampAllCount = builder.stampAllCount;
	}

	// Effective Java Item 2 に基づくビルダー
	public static class Builder {
		private int id;
		private UserStatus status;
		private String name;
		private String password;
		private String email;
		private String zipcode;
		private String address;
		private String telephone;
		private Integer stampNowCount;
		private Integer stampAllCount;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder status(UserStatus status) {
			this.status = status;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder zipcode(String zipcode) {
			this.zipcode = zipcode;
			return this;
		}

		public Builder address(String address) {
			this.address = address;
			return this;
		}

		public Builder telephone(String telephone) {
			this.telephone = telephone;
			return this;
		}

		public Builder stampNowCount(Integer stampNowCount) {
			this.stampNowCount = stampNowCount;
			return this;
		}

		public Builder stampAllCount(Integer stampAllCount) {
			this.stampAllCount = stampAllCount;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

	// 以下getter及びsetter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
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

	public Builder toBuilder() {
		return new Builder()
				.id(this.id)
				.status(this.status)
				.name(this.name)
				.password(this.password)
				.email(this.email)
				.zipcode(this.zipcode)
				.address(this.address)
				.telephone(this.telephone)
				.stampNowCount(this.stampNowCount)
				.stampAllCount(this.stampAllCount);
	}

}
