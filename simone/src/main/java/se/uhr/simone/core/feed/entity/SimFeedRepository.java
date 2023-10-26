package se.uhr.simone.core.feed.entity;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;
import se.uhr.simone.core.entity.SqlScriptRunner;

public class SimFeedRepository extends FeedRepository {

	private static final Logger LOG = LoggerFactory.getLogger(SimFeedRepository.class);

	private final DataSource dataSource;

	public SimFeedRepository(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	protected AtomFeedDAO createAtomFeedDAO(JdbcTemplate jdbcTemplate) {
		return new DerbyAtomFeedDAO(jdbcTemplate);
	}

	public Long getNextSortOrder() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "SELECT COALESCE(MAX(SORT_ORDER),0) FROM ATOM_ENTRY";

		return jdbcTemplate.queryForObject(sql, Long.class) + Long.valueOf(1);
	}

	public void clear() {
		LOG.info("delete all tables");
		SqlScriptRunner runner = new SqlScriptRunner(new JdbcTemplate(dataSource));
		runner.execute(this.getClass().getResourceAsStream("/db/delete_all_tables.sql"));
	}
}
