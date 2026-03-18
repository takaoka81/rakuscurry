package com.example.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.CartItem;
import com.example.domain.User;
import com.example.form.LoginForm;
import com.example.service.CartService;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService service;

	@Autowired
	private CartService cartService;

	@Autowired
	private HttpSession session;

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
