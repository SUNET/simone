package se.uhr.simone.extension.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class Constants {

	public static final URI BASE_URI =
			UriBuilder
					.fromUri(System.getProperty("se.uhr.simone.base.uri",
							System.getenv("SIMONE_BASE_URI") != null ? System.getenv("SIMONE_BASE_URI") : "http://localhost:8080"))
					.build();

	public static final URI REST_URI = UriBuilder.fromUri(BASE_URI).build();

	public static final URI FEED_URI = UriBuilder.fromUri(BASE_URI).segment("feed").build();

}
