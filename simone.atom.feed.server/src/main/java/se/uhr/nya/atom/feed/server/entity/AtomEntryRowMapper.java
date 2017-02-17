package se.uhr.nya.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.nya.util.uuid.TimestampUtil;
import se.uhr.nya.util.uuid.UniqueIdentifier;

class AtomEntryRowMapper implements RowMapper<AtomEntry> {

	@Override
	public AtomEntry mapRow(ResultSet rs, int rowNum) throws SQLException {

		Long feedId = rs.getLong("FEED_ID");
		if (rs.wasNull()) {
			feedId = null;
		}

		return AtomEntry.builder() //
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.of(rs.getBytes("ENTRY_ID")), rs.getString("ENTRY_CONTENT_TYPE"))) //
				.withSortOrder(rs.getLong("SORT_ORDER"))
				.withSubmitted(TimestampUtil.getUTCColumn(rs, "SUBMITTED")) // ,
				.withFeedId(feedId) //
				.withXml(rs.getString("ENTRY_XML")) //
				.build();
	}
}