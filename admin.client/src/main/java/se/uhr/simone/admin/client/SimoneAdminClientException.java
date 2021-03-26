package se.uhr.simone.admin.client;

import javax.ws.rs.core.Response;

public class SimoneAdminClientException extends RuntimeException {

	static final long serialVersionUID = 1L;

	public SimoneAdminClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimoneAdminClientException(String message) {
		super(message);
	}

	public SimoneAdminClientException(String message, Response response) {
		super(message + ", recevied status code: " + response.getStatus());
	}
}
