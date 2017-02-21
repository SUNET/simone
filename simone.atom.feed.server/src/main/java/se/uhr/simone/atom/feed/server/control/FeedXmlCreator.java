package se.uhr.simone.atom.feed.server.control;

import java.net.URI;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.FeedRepository;

@Stateless
public class FeedXmlCreator {

	@Inject
	private FeedConverter feedConverter;

	/**
	 * Fetches all {@link AtomFeed}s that are "full" and converts them to xml if not already converted.
	 * 
	 * @param feedRepository The {@link FeedRepository} to fetch {@link AtomFeed}s from.
	 * @param baseUri The {@link URI} used for building links to next and previous feed. 
	 */
	public void createXmlForFeeds(FeedRepository feedRepository, URI baseUri) {

		List<AtomFeed> feedsWithoutXml = feedRepository.getFeedsWithoutXml();

		for (AtomFeed feed : feedsWithoutXml) {
			feedRepository.saveAtomFeedXml(feed.getId(), feedConverter.convertFeedToXml(feed, baseUri));
		}
	}
}
