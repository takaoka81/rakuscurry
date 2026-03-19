package com.example.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.domain.CartItem;
import com.example.domain.LoginUserDetails;
import com.example.domain.User;
import com.example.service.CartService;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Autowired
        private CartService cartService;

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
                                                                "/inCart", "/showCart", // ショッピングカート関連
                                                                "/searchItem", "/items", "/item/**" // API
                                                                , "/stamp"// スタンプカード
                                                ).permitAll() // 全員許可
                                                .anyRequest().authenticated() // それ以外はログインが必要
                                )
                                .formLogin(login -> login
                                                .loginPage("/toLogin") // ログイン画面
                                                .loginProcessingUrl("/login") // ログイン処理
                                                // 1. 失敗時の遷移（メッセージ表示用）
                                                .failureUrl("/toLogin?error")
                                                // 2. 成功時のカスタム処理（カート登録 & 遷移先決定）
                                                .successHandler(customSuccessHandler())
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

        public AuthenticationSuccessHandler customSuccessHandler() {
                return (request, response, authentication) -> {
                        HttpSession session = request.getSession();

                        // --- DB登録処理 ---
                        LoginUserDetails principal = (LoginUserDetails) authentication.getPrincipal();
                        User user = principal.getUser();

                        @SuppressWarnings("unchecked")
                        List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");

                        if (cartItemList != null && !cartItemList.isEmpty()) {
                                for (CartItem item : cartItemList) {
                                        cartService.addItemToCart(item, user.getId());
                                }
                                session.removeAttribute("cartItemList");
                                session.removeAttribute("totalPrice");
                        }

                        // --- 遷移先決定処理 ---
                        // ログインを求められた元の画面（SavedRequest）があるか確認
                        RequestCache requestCache = new org.springframework.security.web.savedrequest.HttpSessionRequestCache();
                        SavedRequest savedRequest = requestCache.getRequest(request, response);

                        if (savedRequest != null) {
                                // カート画面など、ログインを求められた元のページへ戻す
                                response.sendRedirect(savedRequest.getRedirectUrl());
                        } else {
                                // 直接ログイン画面に来た場合は、商品一覧へ
                                // コンテキストパスを含めたフルパスで指定
                                response.sendRedirect(request.getContextPath() + "/showList");
                        }
                };
        }
}
