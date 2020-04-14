package se.uhr.simone.core.feed.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import se.uhr.simone.api.SimoneTimerEvent;
import se.uhr.simone.atom.feed.server.control.FeedXmlCreator;
import se.uhr.simone.core.control.SimoneConfiguration;
import se.uhr.simone.core.feed.entity.SimFeedRepository;

@ApplicationScoped
public class SimFeedXmlCreator {

	@Inject
	SimoneConfiguration config;

	@Inject
	FeedXmlCreator feedXmlCreator;

	@Inject
	SimFeedRepository feedRepository;

	public void run(@Observes SimoneTimerEvent ev) {
		feedXmlCreator.createXmlForFeeds(feedRepository, config.getFeedBaseURI());
	}
}
