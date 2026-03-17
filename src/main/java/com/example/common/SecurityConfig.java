package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/css/**", "/js/**", "/img_curry/**", // 画像やスタイルシート
                                                                "/toLogin", // ログイン画面
                                                                "/insert", "/insert/insertUser", // 会員登録画面
                                                                "/mailInsert", "/mailsend", "/passCheck", "/check", // メール２段階認証
                                                                "/showList", "/detail", // 商品一覧画面・商品詳細画面
                                                                "/inCart", "/showCart", "/cart/merge", // ショッピングカート関連
                                                                "/searchItem" // API
                                                ).permitAll() // 全員許可
                                                .anyRequest().authenticated() // それ以外はログインが必要
                                )
                                .formLogin(login -> login
                                                .loginPage("/toLogin") // ログイン画面
                                                .loginProcessingUrl("/login") // ログイン処理
                                                .successForwardUrl("/showList")
                                                // .defaultSuccessUrl("/showList", false) // 戻るべきページの記録がない場合 商品一覧画面へ　ログインを求められた画面に
                                                .usernameParameter("email") // ログインフォームのメールアドレス入力欄のname属性の値
                                                .passwordParameter("password") // ログインフォームのpassword入力欄のname属性の値
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // GETでもPOSTでも受け付ける
                                                .logoutSuccessUrl("/toLogin") // ログアウト後の遷移先
                                                .invalidateHttpSession(true) // 追加：HTTPセッションを無効化する
                                                .deleteCookies("JSESSIONID") // 追加：クッキーも削除する
                                                .clearAuthentication(true)); // ログアウト時にAuthentication情報を消去する

                return http.build();
        }

}
