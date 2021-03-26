package se.uhr.simone.admin.client;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.uhr.simone.admin.rs.ResponseBodyRepresentation;
import se.uhr.simone.admin.rs.ResponseRepresentation;

public class RestAdmin {

	private static final String GLOBAL_STATUS_CODE = "/admin/rs/response/code/global";
	private static final String PATH_STATUS_CODE = "/admin/rs/response/code/path";
	private static final String BODY = "/admin/rs/response/body";
	private static final String DELAY = "/admin/rs/response/delay";
	private static final String RATE_LIMIT = "/admin/rs/response/ratelimit";

	private final WebTarget target;

	public RestAdmin(WebTarget target) {
		this.target = target;
	}

	public void setStatusCode(Status status) {
		try (Response response = target.path(GLOBAL_STATUS_CODE).request().put(Entity.text(status.getStatusCode()))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set global status code", response);
			}
		}
	}

	public void resetStatusCode() {
		try (Response response = target.path(GLOBAL_STATUS_CODE).request().delete()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset global status code", response);
			}
		}
	}

	public void setStatusCode(String path, Status status) {
		ResponseRepresentation behavior = ResponseRepresentation.of(path, status.getStatusCode());

		try (Response response = target.path(PATH_STATUS_CODE).request().put(Entity.json(behavior))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set status code for path", response);
			}
		}
	}

	public void resetStatusCode(String path) {
		try (Response response =
				target.path(PATH_STATUS_CODE).request().build("DELETE", Entity.entity(path, MediaType.TEXT_PLAIN)).invoke()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset status code for path", response);
			}
		}
	}

	public void setOverride(String path, Status status, String body) {
		ResponseBodyRepresentation behavior = ResponseBodyRepresentation.of(path, status.getStatusCode(), body);

		try (Response response = target.path(BODY).request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(behavior))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set body for path", response);
			}
		}
	}

	public void resetOverride(String path) {
		try (Response response = target.path(BODY).request().build("DELETE", Entity.entity(path, MediaType.TEXT_PLAIN)).invoke()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset body for path", response);
			}
		}

	}

	public void setDelay(long seconds) {
		try (Response response = target.path(DELAY).request().put(Entity.text(seconds))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set delay", response);
			}
		}
	}

	public void resetDelay() {
		try (Response response = target.path(DELAY).request().put(Entity.text(0))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset delay", response);
			}
		}
	}

	public void setRateLimit(long requestsPerSecond) {
		try (Response response = target.path(RATE_LIMIT).request().put(Entity.text(requestsPerSecond))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not set ratelimit", response);
			}
		}
	}

	public void resetRateLimit() {
		try (Response response = target.path(RATE_LIMIT).request().put(Entity.text(0))) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not reset ratelimit", response);
			}
		}
	}
}
