package se.uhr.simone.atom.feed.server.control;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.uhr.simone.atom.feed.server.control.FeedConverter;
import se.uhr.simone.atom.feed.server.control.FeedXmlCreator;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;

public class FeedXmlCreatorTest {

	private URI testURI;

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private FeedConverter feedConverter;

	@InjectMocks
	private FeedXmlCreator feedXmlCreator = new TestableFeedXmlCreator();

	@Before
	public void setupMockito() throws Exception {
		MockitoAnnotations.initMocks(this);
		testURI = new URI("http://localhost/test-uri");
	}

	@Test
	public void testCreateXmlForFeedsNoEntriesAvailable() {

		given(feedRepository.getFeedsWithoutXml()).willReturn(Collections.EMPTY_LIST);

		feedXmlCreator.createXmlForFeeds(feedRepository, testURI);

		verify(feedRepository, never()).saveAtomFeed(any(AtomFeed.class));
	}

	@Test
	public void testCreateXmlForFeedsOneEntryAvailable() {
		AtomFeed atomFeed = mock(AtomFeed.class);

		given(atomFeed.getId()).willReturn(11L);
		given(feedRepository.getFeedsWithoutXml()).willReturn(Arrays.asList(atomFeed));
		given(feedConverter.convertFeedToXml(atomFeed, testURI)).willReturn("<xml></xml>");

		feedXmlCreator.createXmlForFeeds(feedRepository, testURI);

		verify(feedRepository, times(1)).getFeedsWithoutXml();
		verify(feedConverter, times(1)).convertFeedToXml(atomFeed, testURI);
		verify(feedRepository, times(1)).saveAtomFeedXml(11L, "<xml></xml>");
	}

	private class TestableFeedXmlCreator extends FeedXmlCreator {

	}
}
