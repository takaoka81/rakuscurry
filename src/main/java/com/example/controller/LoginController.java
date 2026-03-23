package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.LoginForm;

@Controller
@RequestMapping("")
public class LoginController {

	@ModelAttribute
	public LoginForm setUpLoginForm() {
		LoginForm loginForm = new LoginForm();
		return loginForm;// リクエストパラメーターにloginFormが格納された
	}

	@RequestMapping("/toLogin")
	public String toLogin() {
		return "login/login";
	}

}
