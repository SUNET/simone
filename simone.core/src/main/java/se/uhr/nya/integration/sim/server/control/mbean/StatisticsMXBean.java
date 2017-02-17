package se.uhr.nya.integration.sim.server.control.mbean;

import javax.management.MXBean;

@MXBean
public interface StatisticsMXBean {

	long getNumberOfRecentRequests();

	long getNumberOfRequestsForFeed(long feedId);

	void clear();
}
