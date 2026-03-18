package com.example.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.form.RandomCheckForm;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

/**
 * ２段階認証を行うコントローラー
 * @author naramasato
 *
 */
@Controller
@RequestMapping("")
public class TwoStepVerificationController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HttpSession session;

	@ModelAttribute
	public RandomCheckForm setUprandomForm() {
		return new RandomCheckForm();
	}
	
	/**
	 * メールアドレス入力画面
	 * @return
	 */
	@RequestMapping("mailInsert")
	public String mailInsert() {
		session.removeAttribute("emailcheck");
		session.removeAttribute("email");
		session.removeAttribute("checkPass");
		return "/insert/mail_insert";
	}
	
	/**
	 * 入力されたメールアドレスに生成された数列を送信
	 * @param form
	 * @return
	 */
	@RequestMapping("mailsend")
	public String mailSend(@Validated RandomCheckForm form, BindingResult result ,Model model, RedirectAttributes redirectAttributes) {
		
		if(result.hasErrors()) {
			return mailInsert();
		}
		if(userService.existsByMailAddress(form.getMail())){
			redirectAttributes.addFlashAttribute("errorMessage", "このメールアドレスは既に登録されています");
			return "redirect:/mailInsert";
		}
		//ランダム生成された整数を受けとる
		String checkPass = userService.randomPass();

		session.setAttribute("checkPass", checkPass);
		session.setAttribute("emailcheck", form.getMail());

		//入力されたメールアドレスに送信
		userService.sendMail(form.getMail(),checkPass);
		
		return "redirect:/passCheck";
	}
	
	@RequestMapping("/passCheck")
	public String passCheck() {
		String emailcheck = (String) session.getAttribute("emailcheck");
		if(emailcheck == null) {
			return "redirect:/mailInsert";
		}
		
		return "/insert/pass_check";
	}
	
	
	/**
	 * 数列を判断
	 * @param form
	 * @param model
	 * @return 正しければ新規登録画面へ遷移
	 */
	@RequestMapping("check")
	public String check(RandomCheckForm form, Model model) {
		if(session.getAttribute("emailcheck") == null) {
			return "redirect:/mailInsert";
		}

		String checkPass = (String) session.getAttribute("checkPass");
		
		String message =  userService.checkpass(form.getPassCheck(), checkPass);
		
		String email = (String) session.getAttribute("emailcheck");
		
		if(message.equals("OK")) {
			session.setAttribute("email", email);
			session.removeAttribute("checkPass");
			return "redirect:/insert";
		} else {
			model.addAttribute("error","入力された値が違います");
			return "/insert/pass_check";
		}
	}
}
