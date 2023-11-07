package se.uhr.simone.atom.feed.server.boundary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import se.uhr.simone.atom.feed.server.control.FeedConverter;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.AbstractFeedRepository;
import se.uhr.simone.feed.server.boundary.FeedResource;

@ExtendWith(MockitoExtension.class)
class FeedResourceTest {

	private static final URI TEST_URI = URI.create("http://localhost/test-uri");

	private final FeedConverter feedConverter = mock(FeedConverter.class);

	private final AbstractFeedRepository feedRepository = mock(AbstractFeedRepository.class);

	private final TestableFeedResource feedResource = new TestableFeedResource(feedConverter, feedRepository);

	@BeforeEach
	public void before() {
		feedResource.clearMap();
	}

	@Test
	void shouldReturnNotFoundWhenFeedDoesNotExist() {

		given(feedRepository.getFeedById(1)).willReturn(null);

		assertThat(feedResource.getFeedXml(1, TEST_URI).getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());

		verify(feedRepository, times(1)).getFeedById(1);
	}

	@Test
	void shouldConvertToXmlIfXmlIsNull() {

		AtomFeed feed = mock(AtomFeed.class);

		given(feedRepository.getFeedById(1)).willReturn(feed);
		given(feed.getXml()).willReturn(null);
		given(feedConverter.convertFeedToXml(feed, TEST_URI)).willReturn("<xml></xml>");

		Response response = feedResource.getFeedXml(1, TEST_URI);

		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());

		assertThat(response.getEntity()).isNotNull();
		System.out.println(response.getEntity());

		verify(feedRepository, times(1)).getFeedById(1);
		verify(feed, times(1)).getXml();
		verify(feedConverter, times(1)).convertFeedToXml(feed, TEST_URI);
	}

	@Test
	void shouldReplaceValuesIfTemplateExists() {

		AtomFeed feed = mock(AtomFeed.class);
		given(feedRepository.getFeedById(1)).willReturn(feed);
		given(feed.getXml()).willReturn(null);
		given(feedConverter.convertFeedToXml(feed, TEST_URI)).willReturn("<xml>_KALLE_</xml>");
		feedResource.add("_KALLE_", "kallepath");
		Response response = feedResource.getFeedXml(1, TEST_URI);
		assertThat(response.getEntity()).isNotNull();
		assertThat(response.getEntity().toString()).isEqualTo("<xml>kallepath</xml>");

	}

	private class TestableFeedResource extends FeedResource {

		Map<String, String> testMap = new HashMap<>();

		public TestableFeedResource(FeedConverter feedConverter, AbstractFeedRepository feedRepository) {
			super(feedConverter, feedRepository);
		}

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
