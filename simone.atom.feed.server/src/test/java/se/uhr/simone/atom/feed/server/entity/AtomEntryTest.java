package se.uhr.simone.atom.feed.server.entity;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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
		assertThat(atomEntry.hasTitle(), is(true));
		assertThat(atomEntry.getTitle(), is("Title"));
	}

	@Test
	public void titleIsNotPresentIfUnset() {
		AtomEntry atomEntry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "application/xml"))
				.withSortOrder(1L)
				.withSubmittedNow()
				.build();
		assertThat(atomEntry.hasTitle(), is(false));
		assertThat(atomEntry.getTitle(), is(nullValue()));
	}

	@Test
	public void titleIsNotPresentIfEmptyString() {
		AtomEntry atomEntry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(UniqueIdentifier.randomUniqueIdentifier(), "application/xml"))
				.withSortOrder(1L)
				.withSubmittedNow()
				.withTitle("  ")
				.build();
		assertThat(atomEntry.hasTitle(), is(false));
		assertThat(atomEntry.getTitle(), is("  "));

	}
}
