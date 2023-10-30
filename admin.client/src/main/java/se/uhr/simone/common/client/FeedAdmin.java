package se.uhr.simone.common.client;

import java.util.Arrays;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uhr.simone.common.HttpConstants;
import se.uhr.simone.common.feed.AtomCategoryRepresentation;
import se.uhr.simone.common.feed.AtomFeedEventRepresentation;

public class FeedAdmin {

	private static final String BLOCK = "/admin/feed/block";
	private static final String RESPONSE_CODE = "/admin/feed/response/code";
	private static final String RESPONSE_DELAY = "/admin/feed/response/delay";
	private static final String EVENT = "/admin/feed/event";

	private final WebTarget target;

	public FeedAdmin(WebTarget target) {
		this.target = target;
	}

	public void block() {
		try (Response response = target.path(BLOCK).request().put(Entity.text(""))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not block simulator", response);
			}
		}
	}

	public void unblock() {
		try (Response response = target.path(BLOCK).request().delete()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not unblock simulator", response);
			}
		}
	}

	public void setStatusCode(Status status) {
		try (Response response = target.path(RESPONSE_CODE).request().put(Entity.text(status.getStatusCode()))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set feed status code", response);
			}
		}
	}

	public void resetStatusCode() {
		try (Response response = target.path(RESPONSE_CODE).request().delete()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not rereset feed status code", response);
			}
		}
	}

	public void setDelay(long seconds) {
		try (Response response = target.path(RESPONSE_DELAY).request().put(Entity.text(seconds))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set feed delay", response);
			}
		}
	}

	public void resetDelay() {
		try (Response response = target.path(RESPONSE_DELAY).request().put(Entity.text(0))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset feed delay", response);
			}
		}
	}

	public String publishEvent(MediaType mediaType, String content, AtomCategory... atomCategories) {
		AtomFeedEventRepresentation event = AtomFeedEventRepresentation.of(mediaType.getType(), content, convert(atomCategories));

		try (Response response = target.path(EVENT).request().post(Entity.entity(event, MediaType.APPLICATION_JSON_TYPE))) {
			if (response.getStatusInfo() == Status.OK) {
				return response.getHeaderString(HttpConstants.EVENT_ID_HEADER);
			} else {
				throw new SimoneAdminClientException("Could not rereset feed status code", response);
			}
		}
	}

	private static AtomCategoryRepresentation[] convert(AtomCategory[] atomCategories) {
		return Arrays.stream(atomCategories)
				.map(c -> AtomCategoryRepresentation.of(c.getTerm(), c.getLabel()))
				.toArray(AtomCategoryRepresentation[]::new);
	}

	public static class AtomCategory {

		private final String term;
		private final String label;

		private AtomCategory(String term, String label) {
			this.term = term;
			this.label = label;
		}

		public static AtomCategory of(String term, String label) {
			return new AtomCategory(term, label);
		}

		public String getTerm() {
			return term;
		}

		public String getLabel() {
			return label;
		}
	}
}
