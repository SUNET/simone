package se.uhr.simone.core.admin.boundary;

import java.io.IOException;
import java.util.Objects;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

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

@Tag(name = "admin")
@AdminCatagory
@Path("admin/feed")
public class FeedResource {

	@Inject
	private SimFeedRepository feedRepository;

	@Inject
	private SimulatedFeedResponse simulatedResponse;

	@Inject
	private FeedBlocker feedBlocker;

	@Operation(summary = "Answer with specified code for all feed requests", description = "Enters a state where all feed requests are answered with the specified status code")
	@PUT
	@Path("response/code")
	public Response setGlobalCode(@Parameter(name = "The HTTP status code", required = true) int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@Operation(summary = "Answer normally for all feed requests", description = "Resumes normal state")
	@DELETE
	@Path("response/code")
	public Response resetGlobalCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		return Response.ok().build();
	}

	@Operation(summary = "Block feed events", description = "Enters a state where no feed event are created")
	@PUT
	@Path("block")
	public Response blockFeed() {
		feedBlocker.setBlocked(true);
		return Response.ok().build();
	}

	@Operation(summary = "Unblock feed events", description = "Resumes normal state")
	@DELETE
	@Path("block")
	public Response unblockFeed() {
		feedBlocker.setBlocked(false);
		return Response.ok().build();
	}

	@Operation(summary = "Delay feed requests", description = "Delay each feed request with the specified time, set 0 to resume to normal")
	@PUT
	@Path("response/delay")
	public Response setDelay(@Parameter(name = "Time in seconds") int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@Operation(summary = "Create a custom feed event")
	@POST
	@Path("event")
	public Response publishEvent(AtomFeedEventRepresentation event) {
		UniqueIdentifier uid = UniqueIdentifier.randomUniqueIdentifier();

		Long nextSortOrder = feedRepository.getNextSortOrder();

		Build builder = AtomEntry.builder()
				.withAtomEntryId(AtomEntryId.of(uid.getValue(), event.getContentType()))
				.withSortOrder(nextSortOrder)
				.withSubmittedNow()
				.withXml(event.getContent());

		for (se.uhr.simone.admin.feed.AtomCategoryRepresentation category : event.getCategorys()) {
			AtomCategory.Build categoryBuilder = AtomCategory.builder().withTerm(Term.of(category.getTerm()));
			if (Objects.nonNull(category.getLabel())) {
				categoryBuilder.withLabel(Label.of(category.getLabel()));
			}
			builder.withCategory(categoryBuilder.build());
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
