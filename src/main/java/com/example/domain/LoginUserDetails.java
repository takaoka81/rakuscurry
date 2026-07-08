package com.example.domain;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUserDetails implements UserDetails, CredentialsContainer {
    private final User user;

    public LoginUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override // ハッシュ化済のパスワードを返す
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // とりあえず空のリストを返してエラーを防ぐ
        return Collections.emptyList();
    }
    
    @Override // ログインで利用するユーザー名を返す
    public String getUsername() {
        return user.getEmail();
    }

    @Override // ユーザーが期限切れでなければtrueを返す
    public boolean isAccountNonExpired() { return true; }

    @Override // ユーザーがロックされていなければtrueを返す
    public boolean isAccountNonLocked() { return true; }

    @Override // ユーザーのパスワードが期限切れでなければtrueを返す
    public boolean isCredentialsNonExpired() { return true; }

    @Override // ユーザーが有効であればtrueを返す
    public boolean isEnabled() { return true; }

    @Override // 認証完了後にSpring Securityから呼ばれ、保持しているパスワードハッシュを消去する
    public void eraseCredentials() {
        user.setPassword(null);
    }

}
