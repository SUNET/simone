package se.uhr.simone.common.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Require a running simone-example server")
class DatabaseIT {

	SimoneAdminClient admin =
			SimoneAdminClient.builder().withClient(ClientBuilder.newClient()).withBaseUri(URI.create("http://localhost:8080")).build();

	@Test
	void shouldLoadDatabaseFromResource() throws Exception {
		List<String> events = admin.database().load("/orders.txt");
		assertThat(events).hasSize(5).allSatisfy(id -> {
			assertThat(id).hasSize(36);
		});
	}

	@Test
	void shouldResetDatabase() {
		assertThatNoException().isThrownBy(() -> admin.database().reset());
	}
}
