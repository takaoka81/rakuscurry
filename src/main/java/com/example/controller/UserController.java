package com.example.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.User;
import com.example.form.InsertForm;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class UserController {

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
    public String deleteUser() {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // 1. データベースのstatusを1にする
            userService.delete(user.getId());
            // 2. セッションを無効化（ログアウト状態にする）
            session.invalidate();
        }
        return "redirect:/toLogin";
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
    public String toUpdateUser(InsertForm form, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            form.setName(user.getName());
            form.setEmail(user.getEmail());
            form.setZipcode(user.getZipcode());
            form.setAddress(user.getAddress());
            form.setTelephone(user.getTelephone());

            return "user/user_update";
        } else {
            return "redirect:/toLogin";
        }
    }

    /**
     * ユーザー情報を更新
     *
     * @param form
     * @param result
     * @return 商品一覧画面
     */
    @PostMapping("/updateUser")
    public String updateUser(@Validated InsertForm form, BindingResult result, Model model) {
        // セッションから現在のパスワードを補完してバリデーションを通す（暫定対応）
        User loginuser = (User) session.getAttribute("user");
        if (form.getPassword() == null || form.getPassword().isEmpty()) {
            form.setPassword(loginuser.getPassword());
            form.setConfirmPassword(loginuser.getPassword());
        }
        if (result.hasFieldErrors("name") || result.hasFieldErrors("address") ||
                result.hasFieldErrors("email") || result.hasFieldErrors("zipcode") ||
                result.hasFieldErrors("telephone")) {

            return "user/user_update";
        }

        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setId(loginuser.getId());
        user.setStatus(0);

        try {
            userService.update(user);
            session.setAttribute("user", user);
            return "redirect:/toLogin";
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            model.addAttribute("emailRegistedError", "そのメールアドレスはすでに使われています");
            return "user/user_update";
        }
    }
}
