package se.uhr.simone.core;

import java.net.URI;
import se.uhr.simone.api.feed.AtomEntry;
import se.uhr.simone.atom.feed.server.control.FeedConverter;
import se.uhr.simone.atom.feed.server.control.FeedCreator;
import se.uhr.simone.atom.feed.server.control.FeedXmlCreator;
import se.uhr.simone.core.feed.control.SimulatorFeedPublisher;
import se.uhr.simone.core.feed.entity.SimFeedRepository;

public class SimOne {

	private final URI feedBaseURI;
	private final Runnable clearDatabaseFunction;

	private final FeedCreator feedCreator;
	private final FeedXmlCreator feedXmlCreator;
	private final SimFeedRepository feedRepository;
	private final SimulatorFeedPublisher feedPublisher;

	private final FeedConverter feedConverter;

	public SimOne(URI feedBaseURI, SimFeedRepository feedRepository, Runnable clearDatabaseFunction) {
		this.feedBaseURI = feedBaseURI;
		this.feedRepository = feedRepository;
		this.clearDatabaseFunction = clearDatabaseFunction;

		feedConverter = new FeedConverter();
		feedCreator = new FeedCreator();
		feedXmlCreator = new FeedXmlCreator(feedConverter);
		feedPublisher = new SimulatorFeedPublisher(feedRepository);
	}

	public URI getFeedBaseURI() {
		return feedBaseURI;
	}

	public void connectEntrysToFeeds() {
		feedCreator.connectEntrysToFeeds(feedRepository);
	}

	public void createXmlForFeeds() {
		feedXmlCreator.createXmlForFeeds(feedRepository, feedBaseURI);
	}

	public void publish(AtomEntry atomEntry) {
		feedPublisher.publish(atomEntry);
	}

	public FeedConverter getFeedConverter() {
		return feedConverter;
	}

	public SimFeedRepository getFeedRepository() {
		return feedRepository;
	}

	public SimulatorFeedPublisher getFeedPublisher() {
		return feedPublisher;
	}

	public void clearDatabase() {
		feedRepository.clear();
		clearDatabaseFunction.run();
	}

	public static SimOne2Builder builder() {
		return new SimOne2Builder();
	}

	public static class SimOne2Builder {

		private URI feedBaseURI;
		private SimFeedRepository feedRepository;
		private Runnable clearDatabaseFunction = () -> {};

		public SimOne2Builder withFeedBaseURI(URI feedBaseURI) {
			this.feedBaseURI = feedBaseURI;
			return this;
		}

		public SimOne2Builder withFeedRepository(SimFeedRepository feedRepository) {
			this.feedRepository = feedRepository;
			return this;
		}

		public SimOne2Builder withClearDatabaseFunction(Runnable clearDatabaseFunction) {
			this.clearDatabaseFunction = clearDatabaseFunction;
			return this;
		}

		public SimOne build() {
			return new SimOne(feedBaseURI, feedRepository, clearDatabaseFunction);
		}
	}
}
