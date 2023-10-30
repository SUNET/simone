package se.uhr.simone.common.client;

import static org.assertj.core.api.Assertions.assertThatNoException;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Require a running simone-example server")
class RestIT {

	SimoneAdminClient admin =
			SimoneAdminClient.builder().withClient(ClientBuilder.newClient()).withBaseUri(URI.create("http://localhost:8080")).build();

	@Test
	void shouldSetStatusCode() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().setStatusCode(Status.NOT_FOUND));
	}

	@Test
	void shouldResetStatusCode() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().resetStatusCode());
	}

	@Test
	void shouldSetStatusCodeForPath() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().setStatusCode("/order", Status.BAD_REQUEST));
	}

	@Test
	void shouldSetRestatusCodeForPath() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().resetStatusCode("/order"));
	}

	@Test
	void shouldSetOverride() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().setOverride("/order", Status.BAD_GATEWAY, "body"));
	}

	@Test
	void shouldResetOverride() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().resetOverride("/order"));
	}

	@Test
	void shouldSetDelay() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().setDelay(2L));
	}

	@Test
	void shouldResetDelay() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().resetDelay());
	}

	@Test
	void shouldSetRateLimit() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().setRateLimit(1L));
	}

	@Test
	void shouldResetRateLinit() throws Exception {
		assertThatNoException().isThrownBy(() -> admin.rest().resetRateLimit());
	}
}
