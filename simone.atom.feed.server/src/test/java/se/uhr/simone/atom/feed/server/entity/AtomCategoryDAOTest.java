package se.uhr.simone.atom.feed.server.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomCategoryDAOTest extends DAOTestCase {

	private AtomCategoryDAO atomCategoryDAO;

	private AtomEntryDAO atomEntryDAO;

	@BeforeEach
	public void setup() {
		atomCategoryDAO = new AtomCategoryDAO(new JdbcTemplate(ds));
		atomEntryDAO = new AtomEntryDAO(new JdbcTemplate(ds));
	}

	@Test
	public void isConnectedShouldReturnFalse() {
		assertThat(atomCategoryDAO.isConnected(createAtomCategory(),
				AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"))).isFalse();
	}

	@Test
	public void isConnectedShouldReturnTrue() {

		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		AtomCategory atomCategory = createAtomCategory();
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), atomCategory);

		assertThat(atomCategoryDAO.isConnected(atomCategory, atomEntry.getAtomEntryId())).isTrue();
	}

	@Test
	public void connectEntryToCategoryShouldThrowExceptionWhenEntryDoesNotExist() {
		assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> {
			atomCategoryDAO.connectEntryToCategory(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"),
					AtomCategory.builder().withTerm(Term.of("term")).withLabel(Label.of("label")).build());
		});
	}

	@Test
	public void getCategoriesForAtomEntryShouldReturnEmptyList() {
		assertThat(atomCategoryDAO.getCategoriesForAtomEntry(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type")))
				.isEmpty();
	}

	@Test
	public void getCategoriesForAtomEntry() {
		AtomEntry atomEntry = createAtomEntry();
		atomEntryDAO.insert(atomEntry);

		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(),
				AtomCategory.builder().withTerm(Term.of("term1")).withLabel(Label.of("label1")).build());
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(),
				AtomCategory.builder().withTerm(Term.of("term2")).withLabel(Label.of("label2")).build());
		atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(),
				AtomCategory.builder().withTerm(Term.of("term3")).withLabel(Label.of("label3")).build());

		assertThat(atomCategoryDAO.getCategoriesForAtomEntry(atomEntry.getAtomEntryId())).hasSize(3);
	}

	private AtomEntry createAtomEntry() {
		return AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "content-type"))
				.withSortOrder(Long.valueOf(1))
				.withSubmittedNow()
				.build();
	}

	private AtomCategory createAtomCategory() {
		return AtomCategory.builder().withTerm(Term.of("term")).withLabel(Label.of("label")).build();
	}
}
