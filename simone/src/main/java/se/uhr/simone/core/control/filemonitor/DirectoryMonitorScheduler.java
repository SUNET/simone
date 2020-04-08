package se.uhr.simone.core.control.filemonitor;

import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.uhr.simone.core.control.SimoneWorker;
import se.uhr.simone.extension.api.SimoneStartupEvent;

@ApplicationScoped
public class DirectoryMonitorScheduler {

	private static final long DELAY = 1_000L;

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMonitorScheduler.class);

	@Inject
	@SimoneWorker
	ManagedExecutor executor;

	@Inject
	private DirectoryMonitor monitor;

	public void init(@Observes SimoneStartupEvent ev) {
		executor.submit(new DirectoryMonitorWorker());
	}

	class DirectoryMonitorWorker implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			while (monitor.isActive()) {
				try {
					monitor.runAvailableJobs();
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception e) {
					LOG.error("dropin directory monitoring failed", e);
				}
			}

			return null;
		}
	}
}
