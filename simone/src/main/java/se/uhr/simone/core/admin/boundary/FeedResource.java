package se.uhr.simone.core.admin.boundary;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import se.uhr.simone.admin.api.HttpConstants;
import se.uhr.simone.admin.feed.AtomFeedEventRepresentation;
import se.uhr.simone.atom.feed.server.entity.AtomCategory;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.Build;
import se.uhr.simone.core.admin.control.FeedBlocker;
import se.uhr.simone.core.admin.control.SimulatedFeedResponse;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.boundary.FeedCatagory;
import se.uhr.simone.core.feed.entity.SimFeedRepository;

@Tag(name = "admin")
@AdminCatagory
@Path("/admin/feed")
@Dependent
public class FeedResource {

	@Inject
	SimFeedRepository feedRepository;

	@Inject
	SimulatedFeedResponse simulatedResponse;

	@Inject
	FeedBlocker feedBlocker;

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Answer with specified code for all feed requests", description = "Enters a state where all feed requests are answered with the specified status code")
	@PUT
	@Path("/response/code")
	public Response setGlobalCode(
			@RequestBody(name = "The HTTP status code", required = true, content = @Content(schema = @Schema(type = SchemaType.INTEGER), example = "401")) int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@Operation(summary = "Answer normally for all feed requests", description = "Resumes normal state")
	@DELETE
	@Path("/response/code")
	public Response resetGlobalCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		return Response.ok().build();
	}

	@Operation(summary = "Block feed events", description = "Enters a state where no feed event are created")
	@PUT
	@Path("/block")
	public Response blockFeed() {
		feedBlocker.setBlocked(true);
		return Response.ok().build();
	}

	@Operation(summary = "Unblock feed events", description = "Resumes normal state")
	@DELETE
	@Path("/block")
	public Response unblockFeed() {
		feedBlocker.setBlocked(false);
		return Response.ok().build();
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Delay feed requests", description = "Delay each feed request with the specified time, set 0 to resume to normal")
	@PUT
	@Path("/response/delay")
	public Response setDelay(
			@RequestBody(name = "Time in seconds", required = true, content = @Content(schema = @Schema(type = SchemaType.INTEGER), example = "401")) int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Operation(summary = "Create a custom feed event")
	@APIResponse(responseCode = "200", headers = @Header(name = "eventId", required = true, description = "The id of the new event", schema = @Schema(type = SchemaType.STRING)))
	@POST
	@Path("/event")
	public Response publishEvent(AtomFeedEventRepresentation event) {
		String uid = UUID.randomUUID().toString();

		Long nextSortOrder = feedRepository.getNextSortOrder();

		Build builder = AtomEntry.builder()
				.withAtomEntryId(uid)
				.withSortOrder(nextSortOrder)
				.withSubmittedNow()
				.withContent(se.uhr.simone.atom.feed.server.entity.Content.builder()
						.withValue(event.getContent())
						.withContentType(event.getContentType())
						.build());

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
