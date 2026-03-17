package com.example.service;


import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.LoginUser;
import com.example.domain.LoginUserDetails;
import com.example.domain.User;
import com.example.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
@Transactional
public class UserService {

	@Autowired
	private HttpSession session;

	@Autowired
	private UserRepository repository;

	@Autowired
	private MailSender sender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User login(String password, String email) {
		User user = repository.findByMailAddress(email);
		if (user == null) {
			return null;
		}
		// パスワードが不一致だった場合はnullを返す
		if (!passwordEncoder.matches(password, user.getPassword())) {
			return null;
		}
		return user;
	}

	/**
	 * ユーザー登録を行う。
	 * 
	 * @param user
	 */
	public void insert(User user) {

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.insert(user);
	}

	/**
	 * ２段階認証のパスワードを入力されたメールアドレスに送信
	 * 
	 * @param email
	 * @param checkPass
	 */
	public void sendMail(String email, String checkPass) {
		SimpleMailMessage msg = new SimpleMailMessage();
		session.setAttribute("checkPass", checkPass);
		session.setAttribute("emailcheck", email);

		msg.setFrom("curry-admin@example.com");
		msg.setTo(email);
		msg.setSubject("２段階認証パスワード発行");// タイトルの設定
		msg.setText(checkPass + " こちらを画面に入力してください"); // 本文の設定

		this.sender.send(msg);
	}

	/**
	 * ランダムの数字を発行
	 * 
	 * @return 生成された４桁の数字
	 */
	public String randomPass() {
		Random rand = new Random();
		String randomStr = "";
		for (int i = 0; i < 4; i++) {
			int num = rand.nextInt(10);
			randomStr += Integer.toString(num);
		}
		System.out.println(randomStr);
		return randomStr;
	}

	/**
	 * 数字の確認
	 * 
	 * @param numPass 入力された数字
	 * @return
	 */
	public String checkpass(String numPass) {
		String checkPass = (String) session.getAttribute("checkPass");

		if (checkPass.equals(numPass)) {
			session.removeAttribute("checkPass");
			return "OK";
		} else {
			return "NO";
		}
	}

}
