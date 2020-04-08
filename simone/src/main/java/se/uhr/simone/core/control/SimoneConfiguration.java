package se.uhr.simone.core.control;

import java.net.URI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class SimoneConfiguration {

	@Inject
	@ConfigProperty(name = "simone.base.uri", defaultValue = "http://localhost:8080")
	URI root;

	public URI getBaseRestURI() {
		return root;
	}

	public URI getFeedBaseURI() {
		return UriBuilder.fromUri(root).segment("feed").build();
	}

}
