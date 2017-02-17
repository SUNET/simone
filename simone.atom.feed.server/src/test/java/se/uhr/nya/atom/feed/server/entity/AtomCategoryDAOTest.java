package se.uhr.nya.atom.feed.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.nya.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.nya.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.nya.util.uuid.UniqueIdentifier;

public class AtomCategoryDAOTest extends DAOTestCase {

	private AtomCategoryDAO atomCategoryDAO;

	private AtomEntryDAO atomEntryDAO;

	@Before
	public void setup() {
		atomCategoryDAO = new AtomCategoryDAO(new JdbcTemplate(db));
		atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(db));
	}

	@Test
	public void isConnectedShouldReturnFalse() {
		assertFalse(atomCategoryDAO.isConnected(createAtomCategory(),
				AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type")));
	}

	@Test
	public void isConnectedShouldReturnTrue() {

		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		AtomCategory atomCategory = createAtomCategory();
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), atomCategory);

		assertTrue(atomCategoryDAO.isConnected(atomCategory, atomEntry.getAtomEntryId()));
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void connectEntryToCategoryShouldThrowExceptionWhenEntryDoesNotExist() {
		atomCategoryDAO.connectEntryToCategory(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"),
				AtomCategory.of(Term.of("term"), Label.of("label")));
	}

	@Test
	public void getCategoriesForAtomEntryShouldReturnEmptyList() {
		assertTrue(atomCategoryDAO.getCategoriesForAtomEntry(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"))
				.isEmpty());
	}

	@Test
	public void getCategoriesForAtomEntry() {
		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), AtomCategory.of(Term.of("term1"), Label.of("label1")));
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), AtomCategory.of(Term.of("term2"), Label.of("label2")));
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), AtomCategory.of(Term.of("term3"), Label.of("label3")));

		assertEquals(3, atomCategoryDAO.getCategoriesForAtomEntry(atomEntry.getAtomEntryId()).size());
	}

	private AtomEntry createAtomEntry() {
		return AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.build();
	}

	private AtomCategory createAtomCategory() {
		return AtomCategory.of(Term.of("term"), Label.of("label"));
	}
}
