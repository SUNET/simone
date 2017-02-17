package se.uhr.nya.integration.sim.server.feed.boundary;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import se.uhr.nya.atom.feed.server.boundary.FeedResource;
import se.uhr.nya.integration.sim.extension.api.Constants;
import se.uhr.nya.integration.sim.server.boundary.FeedCatagory;
import se.uhr.nya.integration.sim.server.control.mbean.Metrics;

@FeedCatagory
@Path("feed")
public class SimulatorFeedResource extends FeedResource {

	@Inject
	private Metrics metricts;

	@Path("recent")
	@GET
	public Response getRecentFeed() {
		metricts.addRecentRequest();

		return super.getRecentFeedXml(Constants.FEED_URI);
	}

	@Path("{id}")
	@GET
	public Response getFeedById(@PathParam("id") long id) {
		metricts.addFeedRequest(id);

		return super.getFeedXml(id, Constants.FEED_URI);
	}

	@Override
	public String replaceTemplateValues(String xml) {
		return xml;
	}

}