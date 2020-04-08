
package se.uhr.simone.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import se.uhr.simone.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.simone.atom.feed.utils.TimestampUtil;

public class AtomEntryDAO {

	static final int MAX_NUM_OF_ENTRIES_TO_RETURN = 10_000;

	private JdbcTemplate jdbcTemplate;

	public AtomEntryDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean exists(String atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_ENTRY WHERE ENTRY_ID=? ");
		return jdbcTemplate.queryForRowSet(sql.toString(), atomEntryId).next();
	}

	public void insert(AtomEntry atomEntry) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				"INSERT INTO ATOM_ENTRY (ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SORT_ORDER, SUBMITTED, TITLE, ENTRY_XML, SUMMARY, SUMMARY_CONTENT_TYPE) VALUES (?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.update(sql.toString(), atomEntry.getAtomEntryId(), atomEntry.getContent().map(getContentType()).orElse(null),
				atomEntry.getFeedId(), atomEntry.getSortOrder(), TimestampUtil.forUTCColumn(atomEntry.getSubmitted()),
				atomEntry.getTitle(), atomEntry.getContent().map(Content::getValue).orElse(null),
				atomEntry.getSummary().map(Content::getValue).orElse(null), atomEntry.getSummary().map(getContentType()).orElse(null));
	}

	public void update(AtomEntry atomEntry) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ATOM_ENTRY SET FEED_ID=?, SUBMITTED=?, TITLE=?, ENTRY_XML=?, ENTRY_CONTENT_TYPE=? WHERE ENTRY_ID=? ");
		jdbcTemplate.update(sql.toString(), atomEntry.getFeedId(), TimestampUtil.forUTCColumn(atomEntry.getSubmitted()),
				atomEntry.getTitle(), atomEntry.getContent().map(Content::getValue).orElse(null),
				atomEntry.getContent().map(getContentType()).orElse(null), atomEntry.getAtomEntryId());
	}

	private Function<? super Content, ? extends String> getContentType() {
		return contentType -> contentType.getContentType().orElse(null);
	}

	public AtomEntry fetchBy(String atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, ENTRY_XML, SUMMARY, SUMMARY_CONTENT_TYPE FROM ATOM_ENTRY WHERE ENTRY_ID = ? ");
		return jdbcTemplate.queryForObject(sql.toString(), new AtomEntryRowMapper(), atomEntryId);
	}

	public List<AtomEntry> getAtomEntriesForFeed(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, ENTRY_XML, SUMMARY, SUMMARY_CONTENT_TYPE FROM ATOM_ENTRY WHERE FEED_ID = ? ORDER BY SORT_ORDER DESC, SUBMITTED DESC");
		return jdbcTemplate.query(sql.toString(), new AtomEntryRowMapper(), id);
	}

	public String getLatestEntryIdForCategory(AtomCategory category) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("SELECT ");
		sql.append("AE.ENTRY_ID ");
		sql.append("FROM ATOM_ENTRY AE ");
		sql.append("inner join ATOM_CATEGORY AC on AE.ENTRY_ID = AC.ENTRY_ID ");
		sql.append("WHERE AC.TERM = ? and AC.LABEL = ? ");
		sql.append("ORDER BY SORT_ORDER DESC, SUBMITTED DESC FETCH FIRST 1 ROWS ONLY ");

		return jdbcTemplate.queryForObject(sql.toString(),
				new Object[] { category.getTerm().getValue(), category.getLabel().map(Label::getValue).orElse(null) },
				new EntryIdRowmapper());
	}

	public List<AtomEntry> getEntriesNotConnectedToFeed() {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, TITLE, ENTRY_XML, SUMMARY, SUMMARY_CONTENT_TYPE FROM ATOM_ENTRY WHERE FEED_ID IS NULL ORDER BY SORT_ORDER ASC, SUBMITTED ASC FETCH FIRST "
						+ MAX_NUM_OF_ENTRIES_TO_RETURN + " ROWS ONLY");
		return jdbcTemplate.query(sql.toString(), new AtomEntryRowMapper());
	}

	private static class EntryIdRowmapper implements RowMapper<String> {

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("ENTRY_ID");

		}
	}
}