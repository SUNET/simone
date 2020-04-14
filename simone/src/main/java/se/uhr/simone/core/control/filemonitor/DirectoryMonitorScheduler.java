package se.uhr.simone.core.control.filemonitor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import se.uhr.simone.api.SimoneTimerEvent;

@ApplicationScoped
public class DirectoryMonitorScheduler {

	@Inject
	private DirectoryMonitor monitor;

	public void run(@Observes SimoneTimerEvent ev) {
		if (monitor.isActive()) {
			monitor.runAvailableJobs();
		}
	}
}
