package se.uhr.nya.integration.sim.extension.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class Constants {

	public static final URI BASE_URI =
			UriBuilder.fromUri(System.getProperty("se.uhr.nya.integration.sim.base.uri", "http://localhost:61080")).build();

	public static final URI REST_URI = UriBuilder.fromUri(BASE_URI).segment("sim", "api").build();

	public static final URI FEED_URI = UriBuilder.fromUri(BASE_URI).segment("sim", "api", "feed").build();

}
