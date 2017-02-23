package se.uhr.simone.core.control.mbean;

import javax.management.MXBean;

@MXBean
public interface StatisticsMXBean {

	long getNumberOfRecentRequests();

	long getNumberOfRequestsForFeed(long feedId);

	void clear();
}
