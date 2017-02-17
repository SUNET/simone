package se.uhr.nya.integration.sim.server.feed.entity;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.nya.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.nya.atom.feed.server.entity.FeedRepository;
import se.uhr.nya.integration.sim.server.entity.FeedDS;

public class SimFeedRepository extends FeedRepository {

	private DataSource dataSource;

	@Inject
	public void setDataSource(@FeedDS DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	protected AtomFeedDAO createAtomFeedDAO(JdbcTemplate jdbcTemplate) {
		return new DerbyAtomFeedDAO(jdbcTemplate);
	}

	public Long getNextSortOrder() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		StringBuilder sql = new StringBuilder("SELECT COALESCE(MAX(SORT_ORDER),0) FROM ATOM_ENTRY");

		return jdbcTemplate.queryForObject(sql.toString(), Long.class) + Long.valueOf(1);
	}

}
