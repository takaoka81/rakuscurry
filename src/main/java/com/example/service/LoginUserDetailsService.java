package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.domain.LoginUserDetails;
import com.example.domain.User;
import com.example.repository.UserRepository;

@Service
public class LoginUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
	public LoginUserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		
		Optional<User> userOp = repository.findByMailAddress(email);
		return userOp.map(user -> new LoginUserDetails(user))
                     .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
	}

}
