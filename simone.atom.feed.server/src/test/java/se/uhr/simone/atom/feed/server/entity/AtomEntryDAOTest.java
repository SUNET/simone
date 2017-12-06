package se.uhr.simone.atom.feed.server.entity;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomEntryDAO;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomEntryDAOTest extends DAOTestCase {

	private static final long FIRST_NON_EXISTING_FEED_ID = 2L;

	private AtomFeedDAO atomFeedDAO;
	private AtomEntryDAO atomEntryDAO;

	@Before
	public void setup() {
		atomFeedDAO = new AtomFeedDAO(new JdbcTemplate(ds));
		atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void notExists() {
		assertFalse(atomEntryDAO.exists(createAtomEntryId()));
	}

	@Test
	public void exists() {
		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		assertTrue(atomEntryDAO.exists(atomEntry.getAtomEntryId()));
	}

	@Test
	public void insert() {
		atomEntryDAO.insert(createAtomEntry());
	}

	@Test
	public void updateNonExisting() {
		atomEntryDAO.update(createAtomEntry());
	}

	@Test
	public void update() {
		AtomEntry atomEntry = createAtomEntry();

		atomEntryDAO.insert(atomEntry);

		atomEntry.setXml("<xml><value>2</value></xml>");

		atomEntryDAO.update(atomEntry);

		AtomEntry fetchedAtomEntry = atomEntryDAO.fetchBy(atomEntry.getAtomEntryId());

		assertEquals(atomEntry.getXml(), fetchedAtomEntry.getXml());
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void fetchByShouldThrowExceptionWhenNotExisting() {
		atomEntryDAO.fetchBy(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "non-existing"));
	}

	@Test
	public void getAtomEntriesForFeedShouldReturnEmptyList() {
		assertTrue(atomEntryDAO.getAtomEntriesForFeed(1).isEmpty());
	}

	@Test
	public void getAtomEntriesForFeedShouldReturnOrderedList() {
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

		List<AtomEntry> entriesForFeed = atomEntryDAO.getAtomEntriesForFeed(1);
		assertEquals(id2, entriesForFeed.get(0).getAtomEntryId().getId());
		assertEquals(id1, entriesForFeed.get(1).getAtomEntryId().getId());
		assertEquals(id3, entriesForFeed.get(2).getAtomEntryId().getId());
	}

	@Test
	public void getAtomEntriesForFeedShouldReturnOrderedListWithSameSubmitTime() {
		atomFeedDAO.insert(new AtomFeed(FIRST_NON_EXISTING_FEED_ID));

		Timestamp now = new Timestamp(DateTime.now().getMillis());

		UniqueIdentifier id1 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id2 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id3 = UniqueIdentifier.randomUniqueIdentifier();
		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id1, "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmitted(now)
				.withFeedId(Long.valueOf(1))
				.build());

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id2, "content-type"))
				.withSortOrder(Long.valueOf(2))
				.withSubmitted(now)
				.withFeedId(Long.valueOf(1))
				.build());

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id3, "content-type"))
				.withSortOrder(Long.valueOf(3))
				.withSubmitted(now)
				.withFeedId(Long.valueOf(1))
				.build());

		for (int i = 1; i <= 2; i++) {
			List<AtomEntry> entriesForFeed = atomEntryDAO.getAtomEntriesForFeed(1);
			assertEquals(id3, entriesForFeed.get(0).getAtomEntryId().getId());
			assertEquals(id2, entriesForFeed.get(1).getAtomEntryId().getId());
			assertEquals(id1, entriesForFeed.get(2).getAtomEntryId().getId());
		}

	}

	@Test
	public void getEntriesNotConnectedToFeedShouldReturnEmptyList() {
		assertTrue(atomEntryDAO.getEntriesNotConnectedToFeed().isEmpty());
	}

	@Test
	public void getEntriesNotConnectedToFeedShouldReturnOrderedList() {

		UniqueIdentifier id1 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id2 = UniqueIdentifier.randomUniqueIdentifier();
		UniqueIdentifier id3 = UniqueIdentifier.randomUniqueIdentifier();
		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id1, "content-type"))
				.withSortOrder(Long.valueOf(2))
				.withSubmittedNow()
				.build());

		DateTimeUtils.setCurrentMillisOffset(1000 * 60 * 2);

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id2, "content-type"))
				.withSortOrder(Long.valueOf(3))
				.withSubmittedNow()
				.build());

		DateTimeUtils.setCurrentMillisOffset(-(1000 * 60 * 20));

		atomEntryDAO.insert(AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(id3, "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.build());

		DateTimeUtils.setCurrentMillisOffset(0);

		List<AtomEntry> entriesNotConnectedToFeed = atomEntryDAO.getEntriesNotConnectedToFeed();
		assertEquals(id3, entriesNotConnectedToFeed.get(0).getAtomEntryId().getId());
		assertEquals(id1, entriesNotConnectedToFeed.get(1).getAtomEntryId().getId());
		assertEquals(id2, entriesNotConnectedToFeed.get(2).getAtomEntryId().getId());
	}

	@Test
	public void getEntriesNotConnectedToFeedShouldReturnMaxNumberOfItems() {
		for (int i = 0; i < AtomEntryDAO.MAX_NUM_OF_ENTRIES_TO_RETURN + 1; i++) {
			UniqueIdentifier id = UniqueIdentifier.randomUniqueIdentifier();
			atomEntryDAO.insert(AtomEntry.builder()
					.withAtomEntryId(AtomEntryId.of(id, "content-type"))
					.withSortOrder(Long.valueOf(1))
					.withSubmittedNow()
					.build());
		}

		List<AtomEntry> entriesNotConnectedToFeed = atomEntryDAO.getEntriesNotConnectedToFeed();
		assertThat(entriesNotConnectedToFeed, hasSize(AtomEntryDAO.MAX_NUM_OF_ENTRIES_TO_RETURN));
	}

	private AtomEntryId createAtomEntryId() {
		return AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type");
	}

	private AtomEntry createAtomEntry() {
		return AtomEntry.builder()
				.withAtomEntryId(createAtomEntryId())
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.withXml("<xml><value>1</value></xml>")
				.build();
	}

}
