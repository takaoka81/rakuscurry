package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
		user.setStampNowCount(rs.getInt("stamp_now_count"));
		user.setStampAllCount(rs.getInt("stamp_all_count"));
		return user;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	public User findByUserId(Integer id) {
		String sql = """
				SELECT
					id,
					name,
					email,
					password,
					zipcode,
					address,
					telephone,
					stamp_now_count,
					stamp_all_count
				FROM
					users
				WHERE
					id=:id;
				""";

		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

		User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
		return user;
	}

	public boolean existsByMailAddress(String email) {
		String sql = "SELECT COUNT(*) FROM users WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		Integer count = template.queryForObject(sql, param, Integer.class);
		return count > 0;
	}

	public User findByMailAddress(String email) {
		String sql = "SELECT * FROM users WHERE email=:email";

		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			System.out.println(user);
			return user;
		} catch (DataAccessException e) {
			return null;
		}

	}

	public void insert(User user) {
		System.out.println(user);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone,stamp_now_count,stamp_all_count) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone, :stampNowCount, :stampAllCount);";
		template.update(sql, param);
	}

}
