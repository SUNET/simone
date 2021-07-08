package se.uhr.simone.atom.feed.server.control;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;

@ExtendWith(MockitoExtension.class)
class FeedCreatorTest {

	private static final long FIRST_ID = 1L;

	@Mock
	private FeedRepository feedRepository;

	private FeedCreator feedCreator = new FeedCreator();

	@Captor
	private ArgumentCaptor<AtomFeed> atomFeedCaptor;

	@Test
	void noEntriesToConnect() {

		given(feedRepository.getEntriesNotConnectedToFeed()).willReturn(Collections.emptyList());

		feedCreator.connectEntrysToFeeds(feedRepository);

		verify(feedRepository, never()).getRecentFeed();
	}

	@Test
	void oneEntryToConnectNoRecentFeedExists() {

		AtomEntry atomEntry = mock(AtomEntry.class);

		given(feedRepository.getEntriesNotConnectedToFeed()).willReturn(Arrays.asList(atomEntry));

		given(feedRepository.getRecentFeed()).willReturn(new AtomFeed(FIRST_ID));

		feedCreator.connectEntrysToFeeds(feedRepository);

		verify(feedRepository, times(1)).getEntriesNotConnectedToFeed();

		verify(feedRepository, times(1)).getRecentFeed();

		verify(feedRepository, times(1)).saveAtomFeed(atomFeedCaptor.capture());

		assertThat(atomFeedCaptor.getValue().getEntries()).hasSize(1);

	}

	@Test
	void oneEntryToConnectRecentFeedExists() {

		AtomEntry atomEntry = mock(AtomEntry.class);

		AtomFeed recentFeed = mock(AtomFeed.class);

		given(feedRepository.getEntriesNotConnectedToFeed()).willReturn(Arrays.asList(atomEntry));

		given(feedRepository.getRecentFeed()).willReturn(recentFeed);

		given(recentFeed.addEntry(atomEntry)).willReturn(true);

		feedCreator.connectEntrysToFeeds(feedRepository);

		verify(feedRepository, times(1)).getEntriesNotConnectedToFeed();
		verify(feedRepository, times(1)).getRecentFeed();
		verify(recentFeed, times(1)).addEntry(atomEntry);
	}

	@Test
	void oneEntryToConnectRecentFeedIsFull() {

		AtomEntry atomEntry = mock(AtomEntry.class);

		AtomFeed recentFeed = mock(AtomFeed.class);

		given(feedRepository.getEntriesNotConnectedToFeed()).willReturn(Arrays.asList(atomEntry));

		given(feedRepository.getRecentFeed()).willReturn(recentFeed);

		given(recentFeed.addEntry(atomEntry)).willReturn(false);

		given(recentFeed.createNextAtomFeed()).willReturn(new AtomFeed(2));

		feedCreator.connectEntrysToFeeds(feedRepository);

		verify(feedRepository, times(1)).getEntriesNotConnectedToFeed();

		verify(feedRepository, times(1)).getRecentFeed();

		verify(recentFeed, times(1)).addEntry(atomEntry);

		verify(recentFeed, times(1)).createNextAtomFeed();

		verify(feedRepository, times(3)).saveAtomFeed(atomFeedCaptor.capture());

		assertThat(atomFeedCaptor.getValue().getId()).isEqualTo(2);

		assertThat(atomFeedCaptor.getValue().getEntries()).hasSize(1);
	}
}
