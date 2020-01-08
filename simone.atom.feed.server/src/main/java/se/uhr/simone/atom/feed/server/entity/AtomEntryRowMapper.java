package se.uhr.simone.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.TimestampUtil;

class AtomEntryRowMapper implements RowMapper<AtomEntry> {

	@Override
	public AtomEntry mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long feedId = rs.getLong("FEED_ID");
		if (rs.wasNull()) {
			feedId = null;
		}

		return AtomEntry.builder() //
				.withAtomEntryId(AtomEntryId.of(rs.getString("ENTRY_ID"), rs.getString("ENTRY_CONTENT_TYPE"))) //
				.withSortOrder(rs.getLong("SORT_ORDER"))
				.withSubmitted(TimestampUtil.getUTCColumn(rs, "SUBMITTED")) // ,
				.withFeedId(feedId) //
				.withTitle(rs.getString("TITLE")) //
				.withXml(rs.getString("ENTRY_XML")) //
				.withSummary(Content.of(rs.getString("SUMMARY")))
				.build();
	}
}