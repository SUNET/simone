package se.uhr.simone.core.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.context.ManagedExecutor;

@ApplicationScoped
public class SimoneExecutorProducer {

	@Produces
	@ApplicationScoped
	@SimoneWorker
	ManagedExecutor executor = ManagedExecutor.builder().build();

	void disposeExecutor(@Disposes @SimoneWorker ManagedExecutor exec) {
		exec.shutdownNow();
	}
}
