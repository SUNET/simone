package se.uhr.simone.core.feed.entity;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AbstractFeedRepository;
import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.core.entity.SqlScriptRunner;

public class DerbyFeedRepository extends AbstractFeedRepository {

	private static final Logger LOG = LoggerFactory.getLogger(DerbyFeedRepository.class);

	private final DataSource dataSource;

	public DerbyFeedRepository(DataSource dataSource) {
		super(dataSource);
		this.dataSource = dataSource;
	}

	@Override
	public AtomFeedDAO createAtomFeedDAO(JdbcTemplate jdbcTemplate) {
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
