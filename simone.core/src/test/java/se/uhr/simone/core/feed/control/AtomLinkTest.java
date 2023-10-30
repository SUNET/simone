package se.uhr.simone.core.feed.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

class AtomLinkTest {

	private static final String URL = "http://example.com/api/resourse/1";

	@Test
	void withRelAlternetive() throws Exception {
		AtomLink link = AtomLink.builder().withRelAlternate().withHref(URL).withType(MediaType.APPLICATION_JSON).build();

		assertThat(link).isNotNull();
		assertThat(link.getRel()).isEqualTo("alternate");
		assertThat(link.getHref()).isEqualTo(URL);
		assertThat(link.getType()).isEqualTo(MediaType.APPLICATION_JSON);
	}

	@Test
	void withCustomRel() throws Exception {
		AtomLink link = AtomLink.builder()
				.withRel("self")
				.withHref("http://example.com/api/resourse/1")
				.withType(MediaType.APPLICATION_XML)
				.build();

		assertThat(link).isNotNull();
		assertThat(link.getRel()).isEqualTo("self");
		assertThat(link.getHref()).isEqualTo(URL);
		assertThat(link.getType()).isEqualTo(MediaType.APPLICATION_XML);
	}

	@Test
	void mustHaveRelValue() throws Exception {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			AtomLink.builder().withRel("").withHref("http://example.com/api/resourse/1").build();
		}).withMessage("Rel must have a value");

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			AtomLink.builder().withRel(null).withHref("http://example.com/api/resourse/1").build();
		}).withMessage("Rel must have a value");
	}

	@Test
	void mustHaveHrefValue() throws Exception {

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			AtomLink.builder().withRel("self").withHref("").build();
		}).withMessage("Href must have a value");

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			AtomLink.builder().withRel("self").withHref(null).build();
		}).withMessage("Href must have a value");
	}
}
