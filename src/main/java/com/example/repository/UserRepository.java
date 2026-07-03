package com.example.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.User;
import com.example.enums.UserStatus;

@Repository
public class UserRepository {

	private static final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setStatus(UserStatus.fromCode(rs.getInt("status")));
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

	public Optional<User> findByUserId(Integer id) {
		String sql = """
				SELECT
					id,
					status,
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

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			return Optional.ofNullable(user);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public boolean existsByMailAddress(String email) {
		String sql = "SELECT COUNT(*) FROM users WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		Integer count = template.queryForObject(sql, param, Integer.class);
		return count > 0;
	}

	public Optional<User> findByMailAddress(String email) {

		String sql = "SELECT * FROM users WHERE email=:email AND status = :status";

		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("email", email)
				.addValue("status", UserStatus.ACTIVE.getCode());

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			return Optional.ofNullable(user);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}

	}

	public void insert(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);

		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone, status,stamp_now_count,stamp_all_count) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone , " + UserStatus.ACTIVE.getCode()
				+ ",:stampNowCount, :stampAllCount);";
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
				.addValue("status", UserStatus.WITHDRAWN.getCode());
		String sql = "UPDATE users SET status = :status WHERE id = :id";
		template.update(sql, param);
	}

}
