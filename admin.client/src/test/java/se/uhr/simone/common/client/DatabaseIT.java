package se.uhr.simone.common.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Require a running simone-example server")
class DatabaseIT {

	SimoneAdminClient admin =
			SimoneAdminClient.builder().withClient(ClientBuilder.newClient()).withBaseUri(URI.create("http://localhost:8080")).build();

	@Test
	void shouldLoadDatabaseFromResource() throws Exception {
		try (InputStream is = DatabaseIT.class.getResourceAsStream("/orders.txt")) {
			List<String> events = admin.database().load("orders.txt", is);
			assertThat(events).hasSize(5).allSatisfy(id -> {
				assertThat(id).hasSize(36);
			});
		}
	}

	@Test
	void shouldResetDatabase() {
		assertThatNoException().isThrownBy(() -> admin.database().reset());
	}
}
