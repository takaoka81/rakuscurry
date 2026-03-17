package com.example.domain;

public class LoginUser {
    /** メールアドレス */
    private String email;
    /** パスワード */
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginUser [email=" + email + ", password=" + password + "]";
    }

}
