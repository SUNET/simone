package se.uhr.simone.common.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.net.URI;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Require a running simone-example server")
class FeedIT {

	SimoneAdminClient admin =
			SimoneAdminClient.builder().withClient(ClientBuilder.newClient()).withBaseUri(URI.create("http://localhost:8080")).build();

	@Test
	void shouldBlock() {
		assertThatNoException().isThrownBy(() -> admin.feed().block());
	}

	@Test
	void shouldUnBlock() {
		assertThatNoException().isThrownBy(() -> admin.feed().unblock());
	}

	@Test
	void shouldSetStatusCode() {
		assertThatNoException().isThrownBy(() -> admin.feed().setStatusCode(Status.NOT_FOUND));
	}

	@Test
	void shouldResetStatusCode() {
		assertThatNoException().isThrownBy(() -> admin.feed().resetStatusCode());
	}

	@Test
	void shouldSetDepliy() {
		assertThatNoException().isThrownBy(() -> admin.feed().setDelay(2L));
	}

	@Test
	void shouldResetDepliy() {
		assertThatNoException().isThrownBy(() -> admin.feed().resetDelay());
	}

	@Test
	void shouldPublishEvent() {
		String eventId = admin.feed().publishEvent(MediaType.APPLICATION_XML_TYPE, "content");
		assertThat(eventId).hasSize(36);
	}
}
