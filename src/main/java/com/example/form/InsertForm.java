package com.example.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録用フォーム
 * @author matsunagadai
 *
 */
public class InsertForm {

	//ユーザー氏名
	@NotBlank(message="名前を入力して下さい")
	private String name;
	//郵便番号
	@NotBlank(message="郵便番号を入力して下さい")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{4}$", message="郵便番号はXXX-XXXXの形式で入力してください")
	private String zipcode;
	//住所
	@NotBlank(message="住所を入力して下さい")
	private String address;
	//電話番号
	@NotBlank(message="電話番号を入力して下さい")
	@Pattern(regexp = "^[0-9]+-[0-9]+-[0-9]+$", message="電話番号はXXXX-XXXX-XXXXの形式で入力してください")
	private String telephone;
	
	//パスワード
	@NotBlank(message="パスワードを入力して下さい")
	@Size(min=8, max=16, message="パスワードは８文字以上１６文字以内で設定してください")
	private String password;
	//確認用パスワード
	@NotBlank(message="確認用パスワードを入力して下さい")
	private String confirmPassword;
	
	//ゲッターセッター
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	//toStringのオーバーライド
	@Override
	public String toString() {
		return "InsertForm [name=" + name + ", zipcode=" + zipcode + ", address=" + address
				+ ", telephone=" + telephone + ", password=" + password + ", confirmPassword=" + confirmPassword + "]";
	}
}
