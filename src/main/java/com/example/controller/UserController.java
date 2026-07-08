package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.domain.LoginUserDetails;
import com.example.domain.User;
import com.example.enums.UserStatus;
import com.example.form.InsertForm;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    /**
     * ユーザー削除（論理削除）
     *
     * @return ログイン画面
     */
    @PostMapping("/deleteUser")
    public String deleteUser(@AuthenticationPrincipal LoginUserDetails loginUserDetails,
            RedirectAttributes redirectAttributes) {
        // 1. データベースのstatusを1にする
        userService.delete(loginUserDetails.getUser().getId());
        // 2. セッションを無効化（ログアウト状態にする）
        session.invalidate();
        redirectAttributes.addFlashAttribute("deleteMessage", "退会手続きが完了しました。ご利用ありがとうございました。");
        return "redirect:/showList";
    }

    /**
     * ユーザー情報を更新画面を表示
     *
     *
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/toUpdateUser")
    public String toUpdateUser(@AuthenticationPrincipal LoginUserDetails loginUserDetails, InsertForm form,
            Model model) {
        User user = loginUserDetails.getUser();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        form.setZipcode(user.getZipcode());
        form.setAddress(user.getAddress());
        form.setTelephone(user.getTelephone());

        return "user/user_update";
    }

    /**
     * ユーザー情報を更新
     *
     * @param form
     * @param result
     * @return 商品一覧画面
     */
    @PostMapping("/updateUser")
    public String updateUser(@AuthenticationPrincipal LoginUserDetails loginUserDetails,
            @Validated InsertForm form, BindingResult result, RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasFieldErrors("name") || result.hasFieldErrors("address") ||
                result.hasFieldErrors("email") || result.hasFieldErrors("zipcode") ||
                result.hasFieldErrors("telephone")) {

            return "user/user_update";
        }

        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setId(loginUserDetails.getUser().getId());
        user.setStatus(UserStatus.ACTIVE);

        try {
            userService.update(user);
            redirectAttributes.addFlashAttribute("updateMessage", "会員情報を更新しました。再度ログインしてください。");
            return "redirect:/toLogin";
        } catch (DataIntegrityViolationException e) {
            logger.error("すでに使われているメールアドレスです", e);
            model.addAttribute("emailRegistedError", "そのメールアドレスはすでに使われています");
            return "user/user_update";
        }
    }
}
