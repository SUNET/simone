package se.uhr.nya.integration.sim.server.control.filemonitor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class MonitorScheduler {

	private static final long DELAY = 1000L;

	private final static Logger LOG = LoggerFactory.getLogger(MonitorScheduler.class);

	@Inject
	private DirectoryMonitor monitor;

	@Resource
	private TimerService timer;

	@PostConstruct
	public void initialize() {
		schedule();
	}

	@Timeout
	public void check() {
		try {
			monitor.runAvailableJobs();
		} catch (Exception e) {
			LOG.error("Failed to run jobs", e);
		} finally {
			schedule();
		}
	}

	private void schedule() {
		timer.createSingleActionTimer(DELAY, new TimerConfig(null, false));
	}
}
