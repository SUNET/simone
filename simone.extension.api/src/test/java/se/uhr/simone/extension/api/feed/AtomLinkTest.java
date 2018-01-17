package se.uhr.simone.extension.api.feed;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

public class AtomLinkTest {

	private static final String URL = "http://example.com/api/resourse/1";

	@Test
	public void withRelAlternetive() throws Exception {
		AtomLink link = AtomLink.builder().withRelAlternate().withHref(URL).withType(MediaType.APPLICATION_JSON).build();

		assertThat(link, is(not(nullValue())));
		assertThat(link.getRel(), is("alternate"));
		assertThat(link.getHref(), is(URL));
		assertThat(link.getType(), is(MediaType.APPLICATION_JSON));
	}

	@Test
	public void withCustomRel() throws Exception {
		AtomLink link = AtomLink.builder()
				.withRel("self")
				.withHref("http://example.com/api/resourse/1")
				.withType(MediaType.APPLICATION_XML)
				.build();

		assertThat(link, is(not(nullValue())));
		assertThat(link.getRel(), is("self"));
		assertThat(link.getHref(), is(URL));
		assertThat(link.getType(), is(MediaType.APPLICATION_XML));
	}

	@Test
	public void mustHaveRelValue() throws Exception {
		try {
			AtomLink.builder().withRel("").withHref("http://example.com/api/resourse/1").build();
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Rel must have a value"));
		}
		try {
			AtomLink.builder().withRel(null).withHref("http://example.com/api/resourse/1").build();
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Rel must have a value"));
		}
	}

	@Test
	public void mustHaveHrefValue() throws Exception {
		try {
			AtomLink.builder().withRel("self").withHref("").build();
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Href must have a value"));
		}
		try {
			AtomLink.builder().withRel("self").withHref(null).build();
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), is("Href must have a value"));
		}
	}
}
