package se.uhr.simone.core.admin.boundary;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.core.Context;
import se.uhr.simone.core.SimOne;
import se.uhr.simone.core.admin.control.ManagedState;
import se.uhr.simone.core.admin.control.ManagedStateRegistry;
import se.uhr.simone.core.admin.control.ManagedFeedResponse;
import se.uhr.simone.core.admin.control.ManagedRSResponse;
import se.uhr.simone.core.admin.control.ManagedRSResponseBody;

public class AdminResource {

	private final DatabaseResource databaseResource;
	private final SimOne simOne;

	private final ManagedFeedResponse simulatedFeedResponse = new ManagedFeedResponse();

	private final ManagedRSResponse simulatedRSResponse = new ManagedRSResponse();

	private final ManagedRSResponseBody simulatedRSResponseBody = new ManagedRSResponseBody();

	public AdminResource(SimOne simOne) {
		this.simOne = simOne;
		databaseResource = new DatabaseResource(simOne);
		ManagedStateRegistry.getInstance().register(simOne.getName(), new ManagedState(simulatedFeedResponse, simulatedRSResponse, simulatedRSResponseBody));
	}

	public AdminResource(SimOne simone, DatabaseResource databaseResource) {
		this.simOne = simone;
		this.databaseResource = databaseResource;
	}

	@Context
	ResourceContext resourceContext;

	@Path("/feed")
	public FeedResource getAdminFeedResource() {
		return resourceContext.initResource(new FeedResource(simOne, simulatedFeedResponse));
	}

	@Path("/rs/response")
	public RSResource getRSResource() {
		return resourceContext.initResource(new RSResource(simulatedRSResponse, simulatedRSResponseBody));
	}

	@Path("/database")
	public DatabaseResource getDatabaseResource() {
		return resourceContext.initResource(databaseResource);
	}
}
