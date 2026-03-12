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
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService service;
	
	@Autowired
	private HttpSession session;
	

	@ModelAttribute
	public LoginForm setUpLoginForm() {
		LoginForm loginForm = new LoginForm();
		return loginForm;//リクエストパラメーターにloginFormが格納された
	}
	
	
	@RequestMapping("/toLogin")
	public String toLogin() {
		return "login/login";
	}
	
	@RequestMapping("/login")
	public String  login(LoginForm form,Model model) {
		//System.out.println(form);
		logger.info("form={}", form);
		User user =service.login(form.getPassword(), form.getEmail());
		//System.out.println(user+"です");
		logger.info("user={} + です", user);
		if(user == null) {
			model.addAttribute("loginError", "メールアドレス、またはパスワードが間違っています");
			return toLogin();//RequestMappingのアドレスを指定
		}
		
		session.setAttribute("user",user);
		
		@SuppressWarnings("unchecked")
		List<CartItem> cartItemList = (List<CartItem>) session.getAttribute("cartItemList");
		
		if(cartItemList == null || cartItemList.size() == 0) {
			return "forward:/showList";
		} else {
			return "redirect:/orderCo";
		}
	}
	
	@RequestMapping("/logout")
	public String logout() {
		session.invalidate();
		return "forward:/showList";
	}
	
}
