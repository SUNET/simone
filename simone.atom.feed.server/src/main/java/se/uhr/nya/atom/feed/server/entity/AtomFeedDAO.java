package se.uhr.nya.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class AtomFeedDAO {

	private JdbcTemplate jdbcTemplate;

	public AtomFeedDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean exists(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_FEED WHERE FEED_ID=?");
		return jdbcTemplate.queryForRowSet(sql.toString(), id).next();
	}

	public void insert(AtomFeed atomFeed) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"INSERT INTO ATOM_FEED (FEED_ID, NEXT_FEED_ID, PREV_FEED_ID, FEED_XML) VALUES (?,?,?, XMLPARSE( DOCUMENT CAST(? AS CLOB(1M)) PRESERVE WHITESPACE))");
		jdbcTemplate.update(sql.toString(), atomFeed.getId(), atomFeed.getNextFeedId(), atomFeed.getPreviousFeedId(),
				atomFeed.getXml());
	}

	public int update(AtomFeed atomFeed) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"UPDATE ATOM_FEED SET NEXT_FEED_ID=?, PREV_FEED_ID=?, FEED_XML=XMLPARSE( DOCUMENT CAST(? AS CLOB(1M)) PRESERVE WHITESPACE) WHERE FEED_ID=?");
		return jdbcTemplate.update(sql.toString(), atomFeed.getNextFeedId(), atomFeed.getPreviousFeedId(), atomFeed.getXml(),
				atomFeed.getId());
	}

	public AtomFeed fetchBy(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT FEED_ID, NEXT_FEED_ID, PREV_FEED_ID, XMLSERIALIZE(FEED_XML AS CLOB(1M)) AS FEED_XML FROM ATOM_FEED WHERE FEED_ID=?");
		return jdbcTemplate.queryForObject(sql.toString(), new AtomFeedRowMapper(), id);
	}

	public AtomFeed fetchRecent() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT F.FEED_ID, F.NEXT_FEED_ID, F.PREV_FEED_ID, XMLSERIALIZE(F.FEED_XML AS CLOB(1M)) AS FEED_XML FROM ATOM_FEED F ORDER BY F.FEED_ID DESC FETCH FIRST 1 ROWS ONLY");
		return jdbcTemplate.queryForObject(sql.toString(), new AtomFeedRowMapper());
	}

	public List<AtomFeed> getFeedsWithoutXml() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT F.FEED_ID, F.NEXT_FEED_ID, F.PREV_FEED_ID, CAST(NULL as CHAR) AS FEED_XML FROM ATOM_FEED F WHERE F.FEED_XML_IS_NULL = 1 AND F.NEXT_FEED_ID IS NOT NULL");
		return jdbcTemplate.query(sql.toString(), new AtomFeedRowMapper());
	}

	public int saveAtomFeedXml(long feedId, String xml) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ATOM_FEED SET FEED_XML=XMLPARSE( DOCUMENT CAST(? AS CLOB(1M)) PRESERVE WHITESPACE) WHERE FEED_ID=?");
		return jdbcTemplate.update(sql.toString(), xml, feedId);
	}

	protected static class AtomFeedRowMapper implements RowMapper<AtomFeed> {

		public AtomFeedRowMapper() {
		}

		@Override
		public AtomFeed mapRow(ResultSet rs, int rowNum) throws SQLException {
			AtomFeed atomFeed = new AtomFeed(rs.getLong("FEED_ID"));
			long NEXT_FEED_ID = rs.getLong("NEXT_FEED_ID");
			if (rs.wasNull()) {
				atomFeed.setNextFeedId(null);
			} else {
				atomFeed.setNextFeedId(NEXT_FEED_ID);
			}

			long previousFeedId = rs.getLong("PREV_FEED_ID");
			if (rs.wasNull()) {
				atomFeed.setPreviousFeedId(null);
			} else {
				atomFeed.setPreviousFeedId(previousFeedId);
			}
			atomFeed.setXml(rs.getString("FEED_XML"));
			return atomFeed;
		}
	}
}
