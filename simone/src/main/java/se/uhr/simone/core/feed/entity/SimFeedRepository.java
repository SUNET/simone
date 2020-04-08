package se.uhr.simone.core.feed.entity;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;
import se.uhr.simone.core.entity.FeedDS;

@Dependent
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
