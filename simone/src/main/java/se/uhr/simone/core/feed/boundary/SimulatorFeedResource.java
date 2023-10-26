package se.uhr.simone.core.feed.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import se.uhr.simone.core.SimOne;
import se.uhr.simone.core.boundary.FeedCatagory;
import se.uhr.simone.feed.server.boundary.FeedResource;

@Tag(name = "feed")
@FeedCatagory
public class SimulatorFeedResource extends FeedResource {

	public final SimOne simOne;

	public SimulatorFeedResource(SimOne simone) {
		super(simone.getFeedConverter(), simone.getFeedRepository());
		this.simOne = simone;
	}

	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Operation(summary = "Get the most recent feed", description = "Get a feed document containing the most recent entries in the feed, see RFC5005 Archived Feeds for more information")
	@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML))
	@Path("/recent")
	@GET
	public Response getRecentFeed() {
		return super.getRecentFeedXml(simOne.getFeedBaseURI());
	}

	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Operation(summary = "Get specific feed", description = "Get the specified feed document, see RFC5005 Archived Feeds for more information")
	@APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_ATOM_XML))
	@Path("/{id}")
	@GET
	public Response getFeedById(@Parameter(name = "id", description = "The feed sequence number") @PathParam("id") long id) {
		return super.getFeedXml(id, simOne.getFeedBaseURI());
	}

	@Override
	public String replaceTemplateValues(String xml) {
		return xml;
	}

}