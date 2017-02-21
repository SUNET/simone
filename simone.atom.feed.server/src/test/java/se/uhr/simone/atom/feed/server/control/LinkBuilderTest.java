package se.uhr.simone.atom.feed.server.control;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.syndication.feed.atom.Link;

import se.uhr.simone.atom.feed.server.control.FeedConverter.LinkBuilder;

public class LinkBuilderTest {

	private static final String baseUrl = "http://baseurl.localhost.com:8080/feeds";

	@BeforeClass
	public static void setupProperties() {
		System.setProperty("feeds.baseurl", baseUrl);
	}

	@Test
	public void testRecent() throws Exception {
		Link recent = LinkBuilder.recent(new URI(System.getProperty("feeds.baseurl") + "/path"));
		assertEquals(baseUrl + "/path/recent", recent.getHref());
		assertEquals(MediaType.APPLICATION_ATOM_XML, recent.getType());
		assertEquals("self", recent.getRel());
	}

	@AfterClass
	public static void removeProperties() {
		System.getProperties().remove("feeds.baseurl");
	}
}
