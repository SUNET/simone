package se.uhr.simone.core.admin.boundary;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.uhr.simone.admin.api.HttpConstants;
import se.uhr.simone.admin.feed.AtomFeedEventRepresentation;
import se.uhr.simone.atom.feed.server.entity.AtomCategory;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.Build;
import se.uhr.simone.atom.feed.utils.UniqueIdentifier;
import se.uhr.simone.core.admin.control.FeedBlocker;
import se.uhr.simone.core.admin.control.SimulatedFeedResponse;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.boundary.FeedCatagory;
import se.uhr.simone.core.feed.entity.SimFeedRepository;

@Api(tags = { "feed admin" })
@AdminCatagory
@Path("admin/feed")
public class FeedResource {

	@Inject
	private SimFeedRepository feedRepository;

	@Inject
	private SimulatedFeedResponse simulatedResponse;

	@Inject
	private FeedBlocker feedBlocker;

	@ApiOperation(value = "Answer with specified code for all feed requests", notes = "Enters a state where all feed requests are answered with the specified status code")
	@PUT
	@Path("response/code")
	public Response setGlobalCode(@ApiParam(value = "The HTTP status code", required = true) int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@ApiOperation(value = "Answer normally for all feed requests", notes = "Resumes normal state")
	@DELETE
	@Path("response/code")
	public Response resetGlobalCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		return Response.ok().build();
	}

	@ApiOperation(value = "Block feed events", notes = "Enters a state where no feed event are created")
	@PUT
	@Path("block")
	public Response blockFeed() {
		feedBlocker.setBlocked(true);
		return Response.ok().build();
	}

	@ApiOperation(value = "Unblock feed events", notes = "Resumes normal state")
	@DELETE
	@Path("block")
	public Response unblockFeed() {
		feedBlocker.setBlocked(false);
		return Response.ok().build();
	}

	@ApiOperation(value = "Delay feed requests", notes = "Delay each feed request with the specified time, set 0 to resume to normal")
	@PUT
	@Path("response/delay")
	public Response setDelay(@ApiParam(value = "Time in seconds") int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@ApiOperation(value = "Create a custom feed event")
	@POST
	@Path("event")
	public Response publishEvent(AtomFeedEventRepresentation event) {
		UniqueIdentifier uid = UniqueIdentifier.randomUniqueIdentifier();

		Long nextSortOrder = feedRepository.getNextSortOrder();

		Build builder = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(uid, event.getContentType()))
				.withSortOrder(nextSortOrder)
				.withSubmittedNow()
				.withXml(event.getContent());

		for (se.uhr.simone.admin.feed.AtomCategoryRepresentation category : event.getCategorys()) {
			builder.withCategory(AtomCategory.of(Term.of(category.getTerm()), Label.of(category.getLabel())));
		}

		feedRepository.saveAtomEntry(builder.build());

		return Response.status(Status.OK).header(HttpConstants.EVENT_ID_HEADER, uid).build();
	}

	@Provider
	@FeedCatagory
	public static class FeedServiceFilter implements ContainerResponseFilter {

		@Inject
		SimulatedFeedResponse simulatedResponse;

		@Override
		public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

			handleDelay();

			if (simulatedResponse.getCode() != SimulatedFeedResponse.NORMAL_STATUS_CODE) {
				responseContext.setStatus(simulatedResponse.getCode());
			}
		}

		private void handleDelay() {
			if (simulatedResponse.getDelay() != 0) {
				try {
					Thread.sleep(simulatedResponse.getDelay() * 1_000L);
				} catch (InterruptedException e) {
					throw new WebApplicationException(e);
				}
			}
		}
	}
}
