package se.uhr.simone.core.admin.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import se.uhr.simone.core.SimOne;
import se.uhr.simone.core.admin.control.SimulatedFeedResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponseBody;

public class AdminResource {

	private final DatabaseResource databaseResource;
	private final SimOne simone;

	private final SimulatedFeedResponse simulatedFeedResponse = new SimulatedFeedResponse();

	private final SimulatedRSResponse simulatedRSResponse = new SimulatedRSResponse();

	private final SimulatedRSResponseBody simulatedRSResponseBody = new SimulatedRSResponseBody();

	public AdminResource(SimOne simone) {
		this.simone = simone;
		databaseResource = new DatabaseResource(simone);
	}

	public AdminResource(SimOne simone, DatabaseResource databaseResource) {
		this.simone = simone;
		this.databaseResource = databaseResource;
	}

	@Context
	ResourceContext resourceContext;

	@Path("/feed")
	public FeedResource getAdminFeedResource() {
		return resourceContext.initResource(new FeedResource(simone, simulatedFeedResponse));
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
