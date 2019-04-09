package se.uhr.simone.atom.feed.server.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomEntryTest {

	@Test
	public void titleIsPresent() {
		AtomEntry atomEntry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "application/xml"))
				.withSortOrder(1L)
				.withSubmittedNow()
				.withTitle("Title")
				.build();
		assertThat(atomEntry.hasTitle()).isTrue();
		assertThat(atomEntry.getTitle()).isEqualTo("Title");
	}

	@Test
	public void titleIsNotPresentIfUnset() {
		AtomEntry atomEntry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "application/xml"))
				.withSortOrder(1L)
				.withSubmittedNow()
				.build();
		assertThat(atomEntry.hasTitle()).isFalse();
		assertThat(atomEntry.getTitle()).isNull();
	}

	@Test
	public void titleIsNotPresentIfEmptyString() {
		AtomEntry atomEntry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "application/xml"))
				.withSortOrder(1L)
				.withSubmittedNow()
				.withTitle("  ")
				.build();
		assertThat(atomEntry.hasTitle()).isFalse();
		assertThat(atomEntry.getTitle()).isEqualTo("  ");

	}
}
