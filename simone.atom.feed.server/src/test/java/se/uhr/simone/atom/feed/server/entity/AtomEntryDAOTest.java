package se.uhr.simone.atom.feed.server.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

@ExtendWith(DataSourceParameterResolver.class)
public class AtomEntryDAOTest {

	private static final long FIRST_NON_EXISTING_FEED_ID = 2L;

	private AtomFeedDAO atomFeedDAO;
	private AtomEntryDAO atomEntryDAO;

	@BeforeEach
	public void setup(DataSource ds) {
		atomFeedDAO = new AtomFeedDAO(new JdbcTemplate(ds));
		atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void notExists() {
		assertThat(atomEntryDAO.exists(createAtomEntryId())).isFalse();
	}

	@Test
	public void exists() {
		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		assertThat(atomEntryDAO.exists(atomEntry.getAtomEntryId())).isTrue();
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

		assertThat(fetchedAtomEntry.getXml()).isEqualTo(atomEntry.getXml());
	}

	@Test
	public void fetchByShouldThrowExceptionWhenNotExisting() {
		assertThatExceptionOfType(EmptyResultDataAccessException.class).isThrownBy(() -> {
			atomEntryDAO.fetchBy(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "non-existing"));
		});
	}

	@Test
	public void getAtomEntriesForFeedShouldReturnEmptyList() {
		assertThat(atomEntryDAO.getAtomEntriesForFeed(1)).isEmpty();
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
		assertThat(entriesForFeed.get(0).getAtomEntryId().getId()).isEqualTo(id2);
		assertThat(entriesForFeed.get(1).getAtomEntryId().getId()).isEqualTo(id1);
		assertThat(entriesForFeed.get(2).getAtomEntryId().getId()).isEqualTo(id3);
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
			assertThat(entriesForFeed.get(0).getAtomEntryId().getId()).isEqualTo(id3);
			assertThat(entriesForFeed.get(1).getAtomEntryId().getId()).isEqualTo(id2);
			assertThat(entriesForFeed.get(2).getAtomEntryId().getId()).isEqualTo(id1);
		}

	}

	@Test
	public void getEntriesNotConnectedToFeedShouldReturnEmptyList() {
		assertThat(atomEntryDAO.getEntriesNotConnectedToFeed()).isEmpty();
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
		assertThat(entriesNotConnectedToFeed.get(0).getAtomEntryId().getId()).isEqualTo(id3);
		assertThat(entriesNotConnectedToFeed.get(1).getAtomEntryId().getId()).isEqualTo(id1);
		assertThat(entriesNotConnectedToFeed.get(2).getAtomEntryId().getId()).isEqualTo(id2);
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
		assertThat(entriesNotConnectedToFeed).hasSize(AtomEntryDAO.MAX_NUM_OF_ENTRIES_TO_RETURN);
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
