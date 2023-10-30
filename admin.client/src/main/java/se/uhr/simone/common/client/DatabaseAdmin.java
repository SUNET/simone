package se.uhr.simone.common.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import se.uhr.simone.admin.api.file.FileLoadResultRepresentation;

public class DatabaseAdmin {

	private static final String PATH = "/admin/database";
	private final WebTarget target;

	public DatabaseAdmin(WebTarget target) {
		this.target = target;
	}

	public List<String> load(String resourceName) {
		MultipartFormDataOutput form = new MultipartFormDataOutput();
		form.addFormData("name", new ByteArrayInputStream(resourceName.getBytes()), MediaType.TEXT_PLAIN_TYPE);

		try (InputStream is = this.getClass().getResourceAsStream(resourceName)) {
			form.addFormData("content", is, MediaType.TEXT_PLAIN_TYPE.withCharset("utf-8"));
			GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(form) {
			};

			try (Response response = target.path(PATH)
					.request(MediaType.APPLICATION_JSON_TYPE)
					.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE))) {
				if (response.getStatusInfo() == Status.OK) {
					FileLoadResultRepresentation result = response.readEntity(FileLoadResultRepresentation.class);

					if (!result.getErrorLog().isEmpty()) {
						return result.getEventIdList();
					} else {
						throw new SimoneAdminClientException(
								"Failed to process resourc: " + resourceName + ", " + result.getErrorLog());
					}

				} else {
					throw new SimoneAdminClientException("Could not load resource: " + resourceName, response);
				}
			}
		} catch (IOException e) {
			throw new SimoneAdminClientException("Could not load resource: " + resourceName, e);
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
