package se.uhr.nya.atom.feed.server.entity;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class AtomFeedDAOTest extends DAOTestCase {

	private static final String TEST_XML_CONTENT = "<xml><value>1</value></xml>";

	private static final long FIRST_NON_EXISTING_FEED_ID = 2L;

	private AtomFeedDAO atomFeedDAO;

	@Before
	public void setup() {
		atomFeedDAO = new AtomFeedDAO(new JdbcTemplate(db));
	}

	@Test
	public void exists() {
		AtomFeed atomFeed = new AtomFeed(FIRST_NON_EXISTING_FEED_ID);
		atomFeedDAO.insert(atomFeed);

		assertTrue(atomFeedDAO.exists(FIRST_NON_EXISTING_FEED_ID));
	}

	@Test
	public void notExists() {
		assertFalse(atomFeedDAO.exists(FIRST_NON_EXISTING_FEED_ID));
	}

	@Test
	public void insert() {
		atomFeedDAO.insert(createAtomFeed());
	}

	@Test
	public void insertShouldHaveNullValues() {
		atomFeedDAO.insert(new AtomFeed(FIRST_NON_EXISTING_FEED_ID));

		AtomFeed fetchedAtomFeed = atomFeedDAO.fetchBy(FIRST_NON_EXISTING_FEED_ID);
		assertNull(fetchedAtomFeed.getNextFeedId());
		assertNull(fetchedAtomFeed.getPreviousFeedId());
		assertNull(fetchedAtomFeed.getXml());
	}

	@Test
	public void updateNotExisting() {
		assertEquals(0, atomFeedDAO.update(createAtomFeed()));
	}

	@Test
	public void update() {

		AtomFeed atomFeed = createAtomFeed();
		atomFeedDAO.insert(atomFeed);

		atomFeed.setNextFeedId(Long.valueOf(24));
		atomFeed.setPreviousFeedId(Long.valueOf(23));
		atomFeed.setXml("<xml><value>2</value></xml>");

		atomFeedDAO.update(atomFeed);

		AtomFeed fetchedAtomFeed = atomFeedDAO.fetchBy(FIRST_NON_EXISTING_FEED_ID);

		assertEquals(atomFeed.getId(), fetchedAtomFeed.getId());
		assertEquals(atomFeed.getNextFeedId(), fetchedAtomFeed.getNextFeedId());
		assertEquals(atomFeed.getPreviousFeedId(), fetchedAtomFeed.getPreviousFeedId());
		assertEquals(atomFeed.getXml(), fetchedAtomFeed.getXml());
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void fetchByNotExisting() {
		atomFeedDAO.fetchBy(FIRST_NON_EXISTING_FEED_ID);
	}

	@Test
	public void fetchBy() {
		AtomFeed atomFeed = createAtomFeed();
		atomFeedDAO.insert(atomFeed);

		AtomFeed fetchedAtomFeed = atomFeedDAO.fetchBy(atomFeed.getId());
		assertEquals(atomFeed.getId(), fetchedAtomFeed.getId());
		assertEquals(atomFeed.getXml(), fetchedAtomFeed.getXml());
	}

	@Test
	public void fetchRecentNothingInserted() {
		AtomFeed feed = atomFeedDAO.fetchRecent();
		assertThat(feed.getId(), is(1L));
	}

	@Test
	public void fetchRecent() {
		atomFeedDAO.insert(createAtomFeed());

		AtomFeed recent = atomFeedDAO.fetchRecent();
		assertEquals(FIRST_NON_EXISTING_FEED_ID, recent.getId());
	}

	@Test
	public void getFeedsWithoutXmlNoFeedsExisting() {
		assertEquals(0, atomFeedDAO.getFeedsWithoutXml().size());
	}

	@Test
	public void getFeedsWithoutXmlOnlyRecentExists() {
		AtomFeed atomFeed = new AtomFeed(2);
		atomFeedDAO.insert(atomFeed);

		assertEquals(0, atomFeedDAO.getFeedsWithoutXml().size());
	}

	@Test
	public void getFeedsWithoutXmlAllFeedsHaveXml() {
		atomFeedDAO.insert(createAtomFeed());
		assertEquals(0, atomFeedDAO.getFeedsWithoutXml().size());
	}

	@Test
	public void getFeedsWithoutXml() {
		AtomFeed atomFeed = createAtomFeed();
		atomFeed.setXml(null);
		atomFeedDAO.insert(atomFeed);

		assertEquals(1, atomFeedDAO.getFeedsWithoutXml().size());
	}

	@Test
	public void testSaveAtomFeedXml() {
		AtomFeed atomFeed = createAtomFeedWithoutXml();
		atomFeedDAO.insert(atomFeed);
		assertThat(atomFeed.getXml(), is(nullValue()));

		assertThat(atomFeedDAO.saveAtomFeedXml(atomFeed.getId(), TEST_XML_CONTENT), is(1));

		AtomFeed atomFeedFromDatabase = atomFeedDAO.fetchBy(atomFeed.getId());
		assertThat(atomFeedFromDatabase.getXml(), is(TEST_XML_CONTENT));
	}

	private AtomFeed createAtomFeed() {
		AtomFeed res = createAtomFeedWithoutXml();
		res.setXml(TEST_XML_CONTENT);
		return res;
	}

	private AtomFeed createAtomFeedWithoutXml() {
		AtomFeed atomFeed = new AtomFeed(FIRST_NON_EXISTING_FEED_ID);
		atomFeed.setNextFeedId((long) 3);
		atomFeed.setPreviousFeedId((long) 1);
		return atomFeed;
	}
}
