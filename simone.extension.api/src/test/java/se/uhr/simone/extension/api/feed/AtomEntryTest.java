package se.uhr.simone.extension.api.feed;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import se.uhr.simone.extension.api.feed.AtomEntry.AtomEntryId;

public class AtomEntryTest {

	@Test
	public void entryMustHaveAlternateLink_whenNoContentIsAdded() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(null, MediaType.APPLICATION_JSON))
				.withSubmittedNow()
				.withAlternateLinks(AtomLink.builder().withRelAlternate().withHref("URL").build())
				.build();
		assertThat(entry, is(not(nullValue())));
		assertThat(entry.getXml(), is(nullValue()));
		assertThat(entry.getLinks(), hasSize(1));
	}

	@Test
	public void entryMustNotHaveTwoAlternateLink_withSameType() throws Exception {
		try {
			AtomEntry.builder()
					.withAtomEntryId(AtomEntryId.of(null, MediaType.APPLICATION_JSON))
					.withSubmittedNow()
					.withAlternateLinks( //
							AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build(),
							AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build())
					.build();
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Alternate links must not have same type"));
		}
	}

	@Test
	public void entryMustAlternateLinks_withDiffernetTypes() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(null, MediaType.APPLICATION_JSON))
				.withSubmittedNow()
				.withAlternateLinks( //
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build(),
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_XML).build())
				.build();

		assertThat(entry, is(not(nullValue())));
		assertThat(entry.getXml(), is(nullValue()));
		assertThat(entry.getLinks(), hasSize(2));
	}
}
