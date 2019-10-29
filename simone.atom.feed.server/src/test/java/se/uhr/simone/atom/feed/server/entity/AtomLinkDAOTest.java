package se.uhr.simone.atom.feed.server.entity;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

@ExtendWith(DataSourceParameterResolver.class)
public class AtomLinkDAOTest {

	private AtomLinkDAO atomLinkDAO;

	private AtomEntryId id = createAtomEntryId();

	@BeforeEach
	public void setup(DataSource ds) {
		atomLinkDAO = new AtomLinkDAO(new JdbcTemplate(ds));
		AtomEntryDAO atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
		atomEntryDAO.insert(AtomEntry.builder().withAtomEntryId(id).withSortOrder(1L).withSubmittedNow().build());
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void notExists() {
		assertThat(atomLinkDAO.exists(id)).isFalse();
	}

	@Test
	public void exists() {
		atomLinkDAO.insert(id, createAtomLink());

		assertThat(atomLinkDAO.exists(id)).isTrue();
	}

	@Test
	public void insert() {
		atomLinkDAO.insert(id, createAtomLink());
	}

	@Test
	public void emptyListWhenNoResult() {
		assertThat(atomLinkDAO.findBy(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "non-existing"))).isEmpty();
	}

	@Test
	public void findsAllLinksForEntryId() {
		atomLinkDAO.insert(id, createAtomLink());
		assertThat(atomLinkDAO.findBy(id)).isNotEmpty();
	}

	@Test
	public void deleteAllLinksForEntry() throws Exception {
		atomLinkDAO.insert(id, createAtomLink());
		atomLinkDAO.delete(id);
		assertThat(atomLinkDAO.findBy(id)).isEmpty();
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
