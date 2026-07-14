package com.example.domain;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUserDetails implements UserDetails, CredentialsContainer {
    private User user;

    public LoginUserDetails(User user) {
        this.user = user;
    }
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // パスワードなどの可変項目を含むUser#hashCode()に委譲すると、
        // eraseCredentials()でパスワードがnullに書き換わった際にhashCodeが変化してしまうため、
        // 不変かつ一意なIDのみを使用する
        result = prime * result + ((user == null) ? 0 : user.getId());
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
        LoginUserDetails other = (LoginUserDetails) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (other.user == null || user.getId() != other.user.getId())
            return false;
        return true;
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
        this.user = user.toBuilder().password(null).build();
    }

}
