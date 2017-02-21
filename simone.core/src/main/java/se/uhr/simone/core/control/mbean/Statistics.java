package se.uhr.simone.core.control.mbean;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class Statistics extends AbstractMBean implements StatisticsMXBean {

	@Inject
	private Metrics metrics;

	public Statistics() {
		super("simulator");
	}

	@Override
	public long getNumberOfRecentRequests() {
		return metrics.getNumberOfRecentRequests();
	}

	@Override
	public long getNumberOfRequestsForFeed(long feedId) {
		return metrics.getNumberOfRequestsForFeed(feedId);
	}

	@Override
	public void clear() {
		metrics.clear();
	}
}
