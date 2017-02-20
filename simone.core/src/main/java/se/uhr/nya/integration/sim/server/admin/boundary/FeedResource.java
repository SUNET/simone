package se.uhr.nya.integration.sim.server.admin.boundary;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import io.swagger.annotations.Api;
import se.uhr.nya.atom.feed.server.entity.AtomCategory;
import se.uhr.nya.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.nya.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.nya.atom.feed.server.entity.AtomEntry;
import se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId;
import se.uhr.nya.atom.feed.server.entity.AtomEntry.Build;
import se.uhr.nya.integration.sim.admin.api.HttpConstants;
import se.uhr.nya.integration.sim.admin.feed.AtomFeedEventRepresentation;
import se.uhr.nya.integration.sim.server.admin.control.FeedBlocker;
import se.uhr.nya.integration.sim.server.admin.control.SimulatedFeedResponse;
import se.uhr.nya.integration.sim.server.boundary.AdminCatagory;
import se.uhr.nya.integration.sim.server.boundary.FeedCatagory;
import se.uhr.nya.integration.sim.server.feed.entity.SimFeedRepository;
import se.uhr.nya.util.uuid.UniqueIdentifier;

@Api(tags = {"feed admin"})
@AdminCatagory
@Path("admin/feed")
public class FeedResource {

	@Inject
	private SimFeedRepository feedRepository;

	@Inject
	private SimulatedFeedResponse simulatedResponse;

	@Inject
	private FeedBlocker feedBlocker;

	@PUT
	@Path("response/code")
	public Response setGlobalCode(int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@DELETE
	@Path("response/code")
	public Response resetGlobalCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		return Response.ok().build();
	}

	@PUT
	@Path("block")
	public Response blockFeed() {
		feedBlocker.setBlocked(true);
		return Response.ok().build();
	}

	@DELETE
	@Path("block")
	public Response unblockFeed() {
		feedBlocker.setBlocked(false);
		return Response.ok().build();
	}

	@PUT
	@Path("response/delay")
	public Response setDelay(int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

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

		for (se.uhr.nya.integration.sim.admin.feed.AtomCategoryRepresentation category : event.getCategorys()) {
			builder.withCategory(AtomCategory.of(Term.of(category.getTerm()), Label.of(category.getLabel())));
		}

		feedRepository.saveAtomEntry(builder.build());

		return Response.status(HttpStatus.SC_OK).header(HttpConstants.EVENT_ID_HEADER, uid).build();
	}

	@Provider
	@ServerInterceptor
	public static class FeedServiceEnablerInterceptor implements PostProcessInterceptor, AcceptedByMethod {

		@Inject
		SimulatedFeedResponse simulatedResponse;

		@Override
		public void postProcess(ServerResponse response) {
			if (simulatedResponse.getCode() != SimulatedFeedResponse.NORMAL_STATUS_CODE) {
				response.setStatus(simulatedResponse.getCode());
			}

			handleDelay();
		}

		@Override
		public boolean accept(Class declaring, Method method) {
			return declaring.isAnnotationPresent(FeedCatagory.class);
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
