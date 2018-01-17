package se.uhr.simone.atom.feed.server.entity;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomLinkDAOTest extends DAOTestCase {

	private AtomLinkDAO atomLinkDAO;

	private AtomEntryId id = createAtomEntryId();

	@Before
	public void setup() {
		atomLinkDAO = new AtomLinkDAO(new JdbcTemplate(ds));
		AtomEntryDAO atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
		atomEntryDAO.insert(AtomEntry.builder().withAtomEntryId(id).withSortOrder(1L).withSubmittedNow().build());
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void notExists() {
		assertFalse(atomLinkDAO.exists(id));
	}

	@Test
	public void exists() {
		atomLinkDAO.insert(id, createAtomLink());

		assertTrue(atomLinkDAO.exists(id));
	}

	@Test
	public void insert() {
		atomLinkDAO.insert(id, createAtomLink());
	}

	@Test
	public void emptyListWhenNoResult() {
		assertThat(atomLinkDAO.findBy(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "non-existing")), is(empty()));
	}

	@Test
	public void findsAllLinksForEntryId() {
		atomLinkDAO.insert(id, createAtomLink());
		assertThat(atomLinkDAO.findBy(id), is(not(empty())));
	}

	@Test
	public void deleteAllLinksForEntry() throws Exception {
		atomLinkDAO.insert(id, createAtomLink());
		atomLinkDAO.delete(id);
		assertThat(atomLinkDAO.findBy(id), is(empty()));
	}

	private AtomEntryId createAtomEntryId() {
		return AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type");
	}

	private AtomLink createAtomLink() {
		return AtomLink.builder()
				.withRelAlternate()
				.withHref("http://exaple.com/api/resource/1")
				.withType(MediaType.APPLICATION_JSON)
				.build();
	}
}
