package com.example.repository;

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

	private static final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
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

	public User findByMailAddress(String email) {
		String sql = "SELECT * FROM users WHERE email=:email AND status = 0";

		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			System.out.println(user);
			return user;
		} catch (Exception e) {
			return null;
		}

	}

	public void insert(User user) {
		System.out.println(user);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone);";
		template.update(sql, param);
	}

	/**
	 * ユーザー情報の更新
	 * @param user
	 */
	public void update(User user){
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "UPDATE users SET name = :name, email = :email, zipcode = :zipcode, address = :address, telephone = :telephone WHERE id = :id";
		template.update(sql, param);
	}

	/**
	 * 論理削除
	 * @param id
	 */
	public void delete(Integer id) {
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("status", 1);
		String sql = "UPDATE users SET status = :status WHERE id = :id";
		template.update(sql, param);
	}

}
