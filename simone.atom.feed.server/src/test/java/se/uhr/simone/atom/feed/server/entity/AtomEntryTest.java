package se.uhr.simone.atom.feed.server.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class AtomEntryTest {

	private static final String ATOM_ENTRY_ID = UUID.randomUUID().toString();

	@Test
	public void titleIsPresent() {
		AtomEntry atomEntry =
				AtomEntry.builder().withAtomEntryId(ATOM_ENTRY_ID).withSortOrder(1L).withSubmittedNow().withTitle("Title").build();
		assertThat(atomEntry.hasTitle()).isTrue();
		assertThat(atomEntry.getTitle()).isEqualTo("Title");
	}

	@Test
	public void titleIsNotPresentIfUnset() {
		AtomEntry atomEntry = AtomEntry.builder().withAtomEntryId(ATOM_ENTRY_ID).withSortOrder(1L).withSubmittedNow().build();
		assertThat(atomEntry.hasTitle()).isFalse();
		assertThat(atomEntry.getTitle()).isNull();
	}

	@Test
	public void titleIsNotPresentIfEmptyString() {
		AtomEntry atomEntry =
				AtomEntry.builder().withAtomEntryId(ATOM_ENTRY_ID).withSortOrder(1L).withSubmittedNow().withTitle("  ").build();
		assertThat(atomEntry.hasTitle()).isFalse();
		assertThat(atomEntry.getTitle()).isEqualTo("  ");

	}
}
