package se.uhr.simone.atom.feed.server.control;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.syndication.feed.atom.Link;

import se.uhr.simone.atom.feed.server.control.FeedConverter.LinkBuilder;

public class LinkBuilderTest {

	private static final String baseUrl = "http://baseurl.localhost.com:8080/feeds";

	@BeforeAll
	public static void setupProperties() {
		System.setProperty("feeds.baseurl", baseUrl);
	}

	@Test
	public void testRecent() throws Exception {
		Link recent = LinkBuilder.recent(new URI(System.getProperty("feeds.baseurl") + "/path"));
		assertThat(recent.getHref()).isEqualTo(baseUrl + "/path/recent");
		assertThat(recent.getType()).isEqualTo(MediaType.APPLICATION_ATOM_XML);
		assertThat(recent.getRel()).isEqualTo("self");
	}

	@AfterAll
	public static void removeProperties() {
		System.getProperties().remove("feeds.baseurl");
	}
}
