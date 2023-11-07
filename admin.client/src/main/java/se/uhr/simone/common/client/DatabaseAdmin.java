package se.uhr.simone.common.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import se.uhr.simone.common.db.FileLoadResultRepresentation;

public class DatabaseAdmin {

	private static final String PATH = "/admin/database";
	private final WebTarget target;

	public DatabaseAdmin(WebTarget target) {
		this.target = target;
	}

	public List<String> load(String name, InputStream is) {
		try {
			final List<EntityPart> multipart = new ArrayList<>();
			multipart.add(
					EntityPart.withName("name")
							.content(name.getBytes())
							.mediaType(MediaType.TEXT_PLAIN_TYPE)
							.build());
			multipart.add(
					EntityPart.withName("content")
							.content(is)
							.mediaType(MediaType.TEXT_PLAIN_TYPE)
							.build());

			try (Response response = target.path(PATH)
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(new GenericEntity<>(multipart) {
					}, MediaType.MULTIPART_FORM_DATA))){

				if (response.getStatusInfo() == Status.OK) {
					FileLoadResultRepresentation result = response.readEntity(FileLoadResultRepresentation.class);

					if (!result.getErrorLog().isEmpty()) {
						return result.getEventIdList();
					} else {
						throw new SimoneAdminClientException(
								"Failed to process resourc: " + name + ", " + result.getErrorLog());
					}
				} else {
					throw new SimoneAdminClientException("Could not load resource: " + name, response);
				}
			}
		} catch (IOException e) {
			throw new SimoneAdminClientException("Could not load resource: " + name, e);
		}
	}

	public void reset() {
		try (Response response = target.path(PATH).request().delete()) {
			if (response.getStatusInfo() != Status.OK) {
				throw new SimoneAdminClientException("Could not block simulator: " + response.getStatus());
			}
		}
	}
}
