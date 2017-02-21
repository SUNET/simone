package se.uhr.simone.core.entity;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.extension.api.config.Config;

@Startup
@Singleton
public class FeedDatabaseInitializer {

	@Inject
	@FeedDS
	private DataSource ds;

	@Inject
	private Config config;

	@PostConstruct
	public void postConstruct() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		SqlScriptRunner runner = new SqlScriptRunner(jdbcTemplate);

		if (config.getInitialization().emptyDataBase()) {

			List<String> tables = getTableNames(jdbcTemplate);

			if (!tables.isEmpty()) {
				tryToDropTables(jdbcTemplate);
			}

			runner.execute(this.getClass().getResourceAsStream("/feed_schema.sql"));
		}
	}

	private void tryToDropTables(JdbcTemplate jdbcTemplate) {
		SqlScriptReader reader = new SqlScriptReader(this.getClass().getResourceAsStream("/feed_drop_tables.sql"));

		for (String stmt : reader.getStatements()) {
			try {
				jdbcTemplate.execute(stmt);
			} catch (DataAccessException e) {
				// OK
			}
		}
	}

	private List<String> getTableNames(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.queryForList("SELECT TABLENAME FROM SYS.SYSTABLES WHERE TABLETYPE='T'", String.class);
	}
}
