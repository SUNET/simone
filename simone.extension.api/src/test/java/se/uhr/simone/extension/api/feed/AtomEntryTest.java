package se.uhr.simone.extension.api.feed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

public class AtomEntryTest {

	@Test
	public void entryMustHaveAlternateLink_whenNoContentIsAdded() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(null)
				.withSubmittedNow()
				.withAlternateLinks(AtomLink.builder().withRelAlternate().withHref("URL").build())
				.build();
		assertThat(entry).isNotNull();
		assertThat(entry.getXml().getValue()).isNull();
		assertThat(entry.getLinks()).hasSize(1);
	}

	@Test
	public void entryMustNotHaveTwoAlternateLink_withSameType() {
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
	public void entryMustAlternateLinks_withDiffernetTypes() throws Exception {
		AtomEntry entry = AtomEntry.builder()
				.withAtomEntryId(null)
				.withSubmittedNow()
				.withAlternateLinks( //
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_JSON).build(),
						AtomLink.builder().withRelAlternate().withHref("URL").withType(MediaType.APPLICATION_XML).build())
				.build();

		assertThat(entry).isNotNull();
		assertThat(entry.getXml().getValue()).isNull();
		assertThat(entry.getLinks()).hasSize(2);
	}
}
