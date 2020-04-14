package se.uhr.simone.core.feed.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import se.uhr.simone.api.SimoneTimerEvent;
import se.uhr.simone.atom.feed.server.control.FeedCreator;
import se.uhr.simone.core.feed.entity.SimFeedRepository;

@ApplicationScoped
public class SimFeedCreator {

	@Inject
	FeedCreator feedCreator;

	@Inject
	SimFeedRepository feedRepository;

	public void run(@Observes SimoneTimerEvent ev) {
		feedCreator.connectEntrysToFeeds(feedRepository);
	}
}
