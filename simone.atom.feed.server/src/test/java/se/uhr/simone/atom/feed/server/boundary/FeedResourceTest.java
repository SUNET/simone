package se.uhr.simone.atom.feed.server.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.uhr.simone.atom.feed.server.control.FeedConverter;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;
import se.uhr.simone.feed.server.boundary.FeedResource;

public class FeedResourceTest {

	private URI testURI;

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private FeedConverter feedConverter;

	@InjectMocks
	private TestableFeedResource feedResource = new TestableFeedResource();

	@BeforeEach
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		testURI = new URI("http://localhost/test-uri");
		feedResource.clearMap();

	}

	@Test
	public void shouldReturnNotFoundWhenFeedDoesNotExist() {

		given(feedRepository.getFeedById(1)).willReturn(null);

		assertThat(feedResource.getFeedXml(1, testURI).getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());

		verify(feedRepository, times(1)).getFeedById(1);
	}

	@Test
	public void shouldConvertToXmlIfXmlIsNull() {

		AtomFeed feed = mock(AtomFeed.class);

		given(feedRepository.getFeedById(1)).willReturn(feed);
		given(feed.getXml()).willReturn(null);
		given(feedConverter.convertFeedToXml(feed, testURI)).willReturn("<xml></xml>");

		Response response = feedResource.getFeedXml(1, testURI);

		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());

		assertThat(response.getEntity()).isNotNull();
		System.out.println(response.getEntity());

		verify(feedRepository, times(1)).getFeedById(1);
		verify(feed, times(1)).getXml();
		verify(feedConverter, times(1)).convertFeedToXml(feed, testURI);
	}

	@Test
	public void shouldReplaceValuesIfTemplateExists() {

		AtomFeed feed = mock(AtomFeed.class);
		given(feedRepository.getFeedById(1)).willReturn(feed);
		given(feed.getXml()).willReturn(null);
		given(feedConverter.convertFeedToXml(feed, testURI)).willReturn("<xml>_KALLE_</xml>");
		feedResource.add("_KALLE_", "kallepath");
		Response response = feedResource.getFeedXml(1, testURI);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity().toString()).isEqualTo("<xml>kallepath</xml>");

	}

	private class TestableFeedResource extends FeedResource {

		Map<String, String> testMap = new HashMap<>();

		@Override
		public String replaceTemplateValues(String xml) {

			return super.replaceValues(xml, testMap.entrySet());
		}

		public void add(String template, String path) {
			testMap.put(template, path);
		}

		public void clearMap() {
			testMap.clear();
		}

	}

}
