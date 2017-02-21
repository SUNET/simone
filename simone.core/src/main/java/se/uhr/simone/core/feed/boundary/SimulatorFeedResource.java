package se.uhr.simone.core.feed.boundary;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import se.uhr.simone.core.boundary.FeedCatagory;
import se.uhr.simone.core.control.mbean.Metrics;
import se.uhr.simone.extension.api.Constants;
import se.uhr.simone.feed.server.boundary.FeedResource;

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