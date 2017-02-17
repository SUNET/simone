package se.uhr.nya.integration.sim.server.control.mbean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Metrics {

	private long recent = 0;

	private ConcurrentMap<Long, AtomicLong> requests = new ConcurrentHashMap<>();

	public long getNumberOfRecentRequests() {
		return recent;
	}

	public long getNumberOfRequestsForFeed(long feedId) {
		return requests.containsKey(feedId) ? requests.get(feedId).get() : 0;
	}

	public void addRecentRequest() {
		recent++;
	}

	public void addFeedRequest(long id) {
		requests.putIfAbsent(id, new AtomicLong());
		requests.get(id).incrementAndGet();
	}

	public void clear() {
		recent = 0;
		requests = new ConcurrentHashMap<>();
	}
}
