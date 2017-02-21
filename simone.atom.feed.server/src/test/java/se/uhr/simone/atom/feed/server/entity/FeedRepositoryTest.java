package se.uhr.simone.atom.feed.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import se.uhr.simone.atom.feed.server.entity.AtomCategory;
import se.uhr.simone.atom.feed.server.entity.AtomCategoryDAO;
import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomEntryDAO;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.AtomFeedDAO;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class FeedRepositoryTest {

	@Mock
	private AtomFeedDAO atomFeedDAO;

	@Mock
	private AtomEntryDAO atomEntryDAO;

	@Mock
	private AtomCategoryDAO atomCategoryDAO;

	@InjectMocks
	private FeedRepository feedRepository = new TestableFeedRepository();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void saveAtomFeedWithoutEntries() {
		AtomFeed atomFeed = new AtomFeed(1);

		given(atomFeedDAO.exists(1)).willReturn(true);

		feedRepository.saveAtomFeed(atomFeed);

		verify(atomFeedDAO, times(1)).exists(1);
		verify(atomFeedDAO, times(1)).update(atomFeed);
		verify(atomEntryDAO, never()).exists(any(AtomEntryId.class));
	}

	@Test
	public void saveAtomFeedWithEntries() {
		AtomFeed atomFeed = new AtomFeed(1);
		AtomEntry atomEntry = createAtomEntry();

		atomFeed.getEntries().add(atomEntry);

		given(atomFeedDAO.exists(1)).willReturn(false);
		given(atomEntryDAO.exists(atomEntry.getAtomEntryId())).willReturn(false);

		feedRepository.saveAtomFeed(atomFeed);

		verify(atomFeedDAO, times(1)).exists(1);
		verify(atomFeedDAO, times(1)).insert(atomFeed);
		verify(atomEntryDAO, times(1)).exists(atomEntry.getAtomEntryId());
		verify(atomEntryDAO, times(1)).insert(atomEntry);
	}

	@Test
	public void saveAtomEntryWithoutCategories() {
		AtomEntry atomEntry = createAtomEntry();

		given(atomEntryDAO.exists(atomEntry.getAtomEntryId())).willReturn(true);

		feedRepository.saveAtomEntry(atomEntry);

		verify(atomEntryDAO, times(1)).exists(atomEntry.getAtomEntryId());
		verify(atomEntryDAO, times(1)).update(atomEntry);
		//		verify(atomCategoryDAO, never()).exists(any(Long.class));
		verify(atomCategoryDAO, never()).isConnected(any(AtomCategory.class), eq(atomEntry.getAtomEntryId()));
	}

	@Test
	public void saveAtomEntryWithCategories() {

	}

	@Test
	public void getFeedByIdNotExisting() {

		given(atomFeedDAO.fetchBy(1)).willThrow(EmptyResultDataAccessException.class);

		AtomFeed feed = feedRepository.getFeedById(1);
		assertNull(feed);

		verify(atomFeedDAO, times(1)).fetchBy(1);
		verify(atomEntryDAO, never()).getAtomEntriesForFeed(1);
	}

	@Test
	public void getFeedById() {
		AtomFeed atomFeed = new AtomFeed(1);

		given(atomFeedDAO.fetchBy(1)).willReturn(atomFeed);
		given(atomEntryDAO.getAtomEntriesForFeed(1)).willReturn(Arrays.asList(createAtomEntry()));

		AtomFeed fetchedFeed = feedRepository.getFeedById(1);

		assertNotNull(fetchedFeed);
		assertEquals(1, fetchedFeed.getEntries().size());

		verify(atomFeedDAO, times(1)).fetchBy(1);
		verify(atomEntryDAO, times(1)).getAtomEntriesForFeed(1);
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void getRecentFeedMustExistInDatabase() {
		given(atomFeedDAO.fetchRecent()).willThrow(EmptyResultDataAccessException.class);
		feedRepository.getRecentFeed();
	}

	@Test
	public void getRecentFeed() {
		AtomFeed atomFeed = new AtomFeed(1);

		given(atomFeedDAO.fetchRecent()).willReturn(atomFeed);
		given(atomEntryDAO.getAtomEntriesForFeed(1)).willReturn(Arrays.asList(createAtomEntry()));

		AtomFeed fetchedFeed = feedRepository.getRecentFeed();

		assertNotNull(fetchedFeed);
		assertEquals(1, fetchedFeed.getEntries().size());

		verify(atomFeedDAO, times(1)).fetchRecent();
		verify(atomEntryDAO, times(1)).getAtomEntriesForFeed(1);
	}

	private AtomEntry createAtomEntry() {
		return AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.withXml("<xml></xml>")
				.build();
	}

	private class TestableFeedRepository extends FeedRepository {

		@Override
		public DataSource getDataSource() {
			return null;
		}
	}
}
