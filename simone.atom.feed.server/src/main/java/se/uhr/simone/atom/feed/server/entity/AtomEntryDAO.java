package se.uhr.simone.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.TimestampUtil;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomEntryDAO {

	static final int MAX_NUM_OF_ENTRIES_TO_RETURN = 10_000;

	private JdbcTemplate jdbcTemplate;

	public AtomEntryDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean exists(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_ENTRY WHERE ENTRY_ID=? AND ENTRY_CONTENT_TYPE=?");
		return jdbcTemplate.queryForRowSet(sql.toString(), atomEntryId.getId().toByteArray(), atomEntryId.getContentType()).next();
	}

	public void insert(AtomEntry atomEntry) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"INSERT INTO ATOM_ENTRY (ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SORT_ORDER, SUBMITTED, TITLE, ENTRY_XML) VALUES (?,?,?,?,?,?, XMLPARSE( DOCUMENT CAST(? AS CLOB(1M)) PRESERVE WHITESPACE))");
		jdbcTemplate.update(sql.toString(), atomEntry.getAtomEntryId().getId().toByteArray(),
				atomEntry.getAtomEntryId() == null ? null : atomEntry.getAtomEntryId().getContentType(), atomEntry.getFeedId(),
				atomEntry.getSortOrder(), TimestampUtil.forUTCColumn(atomEntry.getSubmitted()), atomEntry.getTitle(),
				atomEntry.getXml());
	}

	public void update(AtomEntry atomEntry) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"UPDATE ATOM_ENTRY SET FEED_ID=?, SUBMITTED=?, TITLE=?, ENTRY_XML=XMLPARSE( DOCUMENT CAST(? AS CLOB(1M)) PRESERVE WHITESPACE) WHERE ENTRY_ID=? AND ENTRY_CONTENT_TYPE=?");
		jdbcTemplate.update(sql.toString(), atomEntry.getFeedId(), TimestampUtil.forUTCColumn(atomEntry.getSubmitted()),
				atomEntry.getTitle(), atomEntry.getXml(), atomEntry.getAtomEntryId().getId().toByteArray(),
				atomEntry.getAtomEntryId().getContentType());
	}

	public AtomEntry fetchBy(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, XMLSERIALIZE(ENTRY_XML AS CLOB(1M)) AS ENTRY_XML FROM ATOM_ENTRY WHERE ENTRY_ID = ? AND ENTRY_CONTENT_TYPE = ?");
		return jdbcTemplate.queryForObject(sql.toString(), new AtomEntryRowMapper(), atomEntryId.getId().toByteArray(),
				atomEntryId.getContentType());
	}

	public List<AtomEntry> getAtomEntriesForFeed(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, XMLSERIALIZE(ENTRY_XML AS CLOB(1M)) AS ENTRY_XML FROM ATOM_ENTRY WHERE FEED_ID = ? ORDER BY SORT_ORDER DESC, SUBMITTED DESC");
		return jdbcTemplate.query(sql.toString(), new AtomEntryRowMapper(), id);
	}

	public UniqueIdentifier getLatestEntryIdForCategory(AtomCategory category) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("AE.ENTRY_ID ");
		sql.append("FROM ATOM_ENTRY AE ");
		sql.append("inner join ATOM_CATEGORY AC on AE.ENTRY_ID = AC.ENTRY_ID ");
		sql.append("WHERE AC.TERM = ? and AC.LABEL = ? ");
		sql.append("ORDER BY SORT_ORDER DESC, SUBMITTED DESC FETCH FIRST 1 ROWS ONLY ");
		return jdbcTemplate.queryForObject(sql.toString(),
				new Object[] { category.getTerm().getValue(), category.getLabel().getValue() }, new UuidRowmapper());
	}

	public List<AtomEntry> getEntriesNotConnectedToFeed() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, XMLSERIALIZE(ENTRY_XML AS CLOB(1M)) AS ENTRY_XML FROM ATOM_ENTRY WHERE FEED_ID IS NULL ORDER BY SORT_ORDER ASC, SUBMITTED ASC FETCH FIRST "
						+ MAX_NUM_OF_ENTRIES_TO_RETURN + " ROWS ONLY");
		return jdbcTemplate.query(sql.toString(), new AtomEntryRowMapper());
	}

	private static class UuidRowmapper implements RowMapper<UniqueIdentifier> {

		@Override
		public UniqueIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
			return UniqueIdentifier.of(rs.getBytes("ENTRY_ID"));

		}
	}
}