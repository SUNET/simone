package se.uhr.simone.core.entity;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

@Startup
@Singleton
public class FeedDatabaseInitializer {

	@Inject
	@FeedDS
	private DataSource ds;

	@PostConstruct
	public void postConstruct() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		
		 int count = jdbcTemplate.queryForObject("select count(*) from SYS.SYSTABLES where TABLENAME = 'ATOM_FEED'", Integer.class);
		 
		 if(count == 0) {
			 SqlScriptRunner runner = new SqlScriptRunner(jdbcTemplate);
			 runner.execute(this.getClass().getResourceAsStream("/feed_schema.sql"));
		 }
	}
}
