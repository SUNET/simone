package se.uhr.simone.core.feed.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

class AtomEntryTest {

	@Test
	void entryMustHaveAlternateLink_whenNoContentIsAdded() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(null)
				.withSubmittedNow()
				.withAlternateLinks(AtomLink.builder().withRelAlternate().withHref("URL").build())
				.build();
		assertThat(entry).isNotNull();
		assertThat(entry.getContent()).isNull();
		assertThat(entry.getLinks()).hasSize(1);
	}

	@Test
	void entryMustNotHaveTwoAlternateLink_withSameType() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			AtomEntry.builder()
					.withAtomEntryId(null)
					.withSubmittedNow()
					.withAlternateLinks( //
							AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build(),
							AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build())
					.build();
		}).withMessage("Alternate links must not have same type");
	}

	@Test
	void entryMustAlternateLinks_withDiffernetTypes() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(null)
				.withSubmittedNow()
				.withAlternateLinks( //
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build(),
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_XML).build())
				.build();

		assertThat(entry).isNotNull();
		assertThat(entry.getContent()).isNull();
		assertThat(entry.getLinks()).hasSize(2);
	}
}
