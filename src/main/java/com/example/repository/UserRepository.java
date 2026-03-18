package com.example.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

	private static final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
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

		String sql = "SELECT * FROM users WHERE email=:email AND status = 0";

		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			logger.info("user={}", user);
			return user;
		} catch (DataAccessException e) {
			return null;
		}

	}

	public void insert(User user) {
		logger.info("user={}", user);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);

		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone, status,stamp_now_count,stamp_all_count) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone , 0,:stampNowCount, :stampAllCount);";
		template.update(sql, param);
	}

	/**
	 * ユーザテーブルのスタンプカウントの更新を行います
	 * 
	 * @param user 登録する情報
	 */
	public void updateStampCounts(User user) {
		String sql = """
				UPDATE
					users
				SET
					stamp_now_count=:stampNowCount,
					stamp_all_count=:stampAllCount
				WHERE
					id=:id;
				""";
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		template.update(sql, param);
	}

	/**
	 * ユーザー情報の更新
	 * 
	 * @param user
	 */

	public void update(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "UPDATE users SET name = :name, email = :email, zipcode = :zipcode, address = :address, telephone = :telephone WHERE id = :id";
		template.update(sql, param);
	}

	/**
	 * 論理削除
	 * 
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
