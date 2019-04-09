package se.uhr.simone.atom.feed.server.control;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;

@ExtendWith(MockitoExtension.class)
public class FeedXmlCreatorTest {

	private static final URI testURI = URI.create("http://localhost/test-uri");

	@Mock
	private FeedRepository feedRepository;

	@Mock
	private FeedConverter feedConverter;

	@InjectMocks
	private FeedXmlCreator feedXmlCreator = new TestableFeedXmlCreator();

	@Test
	public void testCreateXmlForFeedsNoEntriesAvailable() {
		given(feedRepository.getFeedsWithoutXml()).willReturn(Collections.emptyList());

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
