package se.uhr.simone.atom.feed.server.entity;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomEntryDAO;
import se.uhr.simone.atom.feed.server.entity.AtomEntrySearchDAO;
import se.uhr.simone.atom.feed.server.entity.AtomEntrySearchParams;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomEntrySearchDAOTest extends DAOTestCase {

	private static final long FIRST_NON_EXISTING_FEED_ID = 2L;

	private AtomFeedDAO atomFeedDAO;
	private AtomEntryDAO atomEntryDAO;
	private AtomEntrySearchDAO atomEntrySearchDAO;

	@Before
	public void setup() {
		atomEntrySearchDAO = new AtomEntrySearchDAO(new JdbcTemplate(ds));
		atomFeedDAO = new AtomFeedDAO(new JdbcTemplate(ds));
		atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void testSimpleSearchWithHits() {

		atomFeedDAO.insert(new AtomFeed(FIRST_NON_EXISTING_FEED_ID));

		UniqueIdentifier id1 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id2 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id3 = UniqueIdentifier.randomUniqueIdentifier();
		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id1, "content-type"))
				.withSortOrder(Long.valueOf(2))
				.withSubmittedNow()
				.withFeedId(Long.valueOf(1))
				.build());

		DateTimeUtils.setCurrentMillisOffset(1000 * 60 * 2);

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id2, "content-type"))
				.withSortOrder(Long.valueOf(3))
				.withSubmittedNow()
				.withFeedId(Long.valueOf(1))
				.build());

		DateTimeUtils.setCurrentMillisOffset(-(1000 * 60 * 20));

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id3, "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.withFeedId(Long.valueOf(1))
				.build());

		DateTimeUtils.setCurrentMillisOffset(0);

		AtomEntrySearchParams searchParams = new AtomEntrySearchParams(null, null, null, null, null, null, null, 2);
		List<AtomEntry> searchAtomEntriesUR = atomEntrySearchDAO.searchAtomEntriesUR(searchParams);

		assertEquals(2, searchAtomEntriesUR.size());
		assertEquals(id2, searchAtomEntriesUR.get(0).getAtomEntryId().getId());
		assertEquals(id1, searchAtomEntriesUR.get(1).getAtomEntryId().getId());

	}
}
