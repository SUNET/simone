package se.uhr.simone.extension.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class SimoneProperties {

	private static final URI BASE_URI =
			UriBuilder
					.fromUri(System.getProperty("se.uhr.simone.base.uri",
							System.getenv("SIMONE_BASE_URI") != null ? System.getenv("SIMONE_BASE_URI") : "http://localhost:8080"))
					.build();

	private SimoneProperties() {

	}

	public static URI getBaseRestURI() {
		return UriBuilder.fromUri(BASE_URI).build();
	}

	public static URI getFeedBaseURI() {
		return UriBuilder.fromUri(BASE_URI).segment("feed").build();
	}

}
