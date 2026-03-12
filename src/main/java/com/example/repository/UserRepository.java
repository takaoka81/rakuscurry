package com.example.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.User;

@Repository
public class UserRepository {

	private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
	
	private static final RowMapper<User> USER_ROW_MAPPER =(rs,i)->{
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setTelephone(rs.getString("telephone"));
		return user;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;
	
	public User  findByMailAddress(String email) {
		String sql ="SELECT * FROM users WHERE email=:email";
		
		SqlParameterSource param = new MapSqlParameterSource().addValue("email",email);
		
		try {
			User user= template.queryForObject(sql, param, USER_ROW_MAPPER);
			//System.out.println(user);
			logger.info("user={}", user);
			return user;
		}catch(Exception e) {
			return null;
		}
		
	}
	
	public void insert(User user) {
		//System.out.println(user);
		logger.info("user={}", user);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone);";	
		template.update(sql, param);		
	}
	
}
