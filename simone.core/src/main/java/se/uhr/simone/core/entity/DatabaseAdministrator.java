package se.uhr.simone.core.entity;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.extension.api.entity.DatabaseAdmin;

public class DatabaseAdministrator implements DatabaseAdmin {

	private JdbcTemplate jdbcTemplate;

	@Inject
	public void setDataSource(@FeedDS DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void dropTables() {
		SqlScriptRunner runner = new SqlScriptRunner(jdbcTemplate);
		runner.execute(this.getClass().getResourceAsStream("/feed_drop_tables.sql"));
		runner.execute(this.getClass().getResourceAsStream("/feed_schema.sql"));
	}
}
